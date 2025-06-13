package matlabComparisonTests;

import com.uppaal.model.core2.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import polimi.logic.generator.UppaalModelGenerator;
import polimi.model.Network;
import polimi.logic.NetworkBuilder;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ModelGeneratorComparisonTest {

    private static final String JSON_DIR = "example/jsonNet";
    private static final String XML_DIR = "example/generated";

    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    public void testGeneratedModelMatchesMatlab(File jsonFile) throws Exception {

        String baseName = jsonFile.getName().replace(".json", "");
        System.out.print(baseName);
        File xmlFile = new File(XML_DIR, baseName + ".xml");
        assertTrue(xmlFile.exists(), "Missing XML file: " + xmlFile.getAbsolutePath());

        Document expectedModel = Document.load(xmlFile.getAbsolutePath());
        long start = System.nanoTime();
        Network actualNetwork = NetworkBuilder.build(jsonFile);
        Document actualModel = new UppaalModelGenerator(actualNetwork).generateDocument(true);
        long end = System.nanoTime();
        System.out.println(": " + ((end-start) / 1_000_000.0) + "ms");

        compareGlobalDeclarations(expectedModel, actualModel);
        compareAllTemplates(expectedModel, actualModel);
        compareQuery(expectedModel, actualModel);
    }

    static Stream<File> jsonFilesProvider() {
        File dir = new File(JSON_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("Directory does not exist: " + JSON_DIR);
        }

        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(f -> f.getName().endsWith(".json"));
    }

    private void compareGlobalDeclarations(Document expected, Document actual) {
        String expectedDecl = normalize(expected.getProperty("declaration").getValue());
        String actualDecl = normalize(actual.getProperty("declaration").getValue());
        assertEquals(expectedDecl, actualDecl, "Global declarations do not match.");
    }
    private void compareQuery(Document expected, Document actual){
        String expectedQuery = expected.getQueryList().get(0).getFormula();
        String actualQuery = actual.getQueryList().get(0).getFormula();

        List<String> expectedConditions = extractOrConditions(expectedQuery);
        List<String> actualConditions = extractOrConditions(actualQuery);
        Collections.sort(expectedConditions);
        Collections.sort(actualConditions);

        assertEquals(String.join("\n",expectedConditions), String.join("\n",actualConditions), "Queries do not match.");
    }

    private void compareAllTemplates(Document expected, Document actual) {
        List<AbstractTemplate> expectedTemplates = expected.getTemplateList();
        List<AbstractTemplate> actualTemplates = actual.getTemplateList();

        assertEquals(expectedTemplates.size(), actualTemplates.size(), "Number of templates differ.");

        for (int i = 0; i < expectedTemplates.size(); i++) {
            Template exp = (Template) expectedTemplates.get(i);
            Template act = (Template) actualTemplates.get(i);
            String nameExp = exp.getPropertyValue("name");
            String nameAct = act.getPropertyValue("name");

            assertEquals(nameExp, nameAct, "Template names differ at index " + i);

            compareLocations(exp, act, nameExp);
            compareTransitions(exp, act, nameExp);
        }
    }

    private void compareLocations(Template expected, Template actual, String templateName) {
        Set<String> expectedLocNames = extractLocationNames(expected);
        Set<String> actualLocNames = extractLocationNames(actual);

        assertEquals(expectedLocNames, actualLocNames, "Location names mismatch in template: " + templateName);
    }

    private void compareTransitions(Template expected, Template actual, String templateName) {
        List<TransitionStub> expectedEdges = extractTransitions(expected);
        List<TransitionStub> actualEdges = extractTransitions(actual);

        for (TransitionStub e : expectedEdges) {
            boolean match = actualEdges.stream().anyMatch(e::matches);

            assertTrue(match, "Missing transition in template '" + templateName +
                    "' from '" + e.source + "' to '" + e.target + "' with labels " + e.labels);
        }
    }

    private List<Location> getLocations(Template template) {
        List<Location> locations = new ArrayList<>();
        for (Node n = template.first; n != null; n = n.getNext()) {
            if (n instanceof Location) {
                locations.add((Location) n);
            }
        }
        return locations;
    }

    private List<Edge> getEdges(Template template) {
        List<Edge> edges = new ArrayList<>();
        for (Node n = template.first; n != null; n = n.getNext()) {
            if (n instanceof Edge) {
                edges.add((Edge) n);
            }
        }
        return edges;
    }

    private Set<String> extractLocationNames(Template template) {
        return getLocations(template).stream()
                .map(loc -> Objects.toString(loc.getPropertyValue("name"), "").trim())
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());
    }

    private List<TransitionStub> extractTransitions(Template template) {
        return getEdges(template).stream()
                .map(edge -> new TransitionStub(
                        Objects.toString(edge.getSource().getPropertyValue("name"), "").trim(),
                        Objects.toString(edge.getTarget().getPropertyValue("name"), "").trim(),
                        Map.of(
                                "guard", normalize(edge.getProperty("guard").getValue()),
                                "synchronisation", normalize(edge.getProperty("synchronisation").getValue()),
                                "assignment", normalize(edge.getProperty("assignment").getValue())
                        )
                ))
                .collect(Collectors.toList());
    }

    private String normalize(String s) {
        if (s == null) return "";

        // Normalizzazione base
        s = s.replaceAll("[ \\t\\r]+", "")
                .replaceAll("\\n{2,}", "\n")
                .trim();

        // Normalizzazione delle equazioni tipo: Irc_x = Irc_a - Irc_b - Irc_c
        return Arrays.stream(s.split("\n"))
                .map(this::normalizeIrcEquation)
                .collect(Collectors.joining("\n"));
    }

    private String normalizeIrcEquation(String line) {
        if (!line.contains("=Irc_") || !line.contains("-")) return line;

        String[] parts = line.split("=");
        if (parts.length != 2) return line;

        String lhs = parts[0].trim();  // es: Irc_1
        String rhs = parts[1].trim();  // es: Irc_5 - Irc_3 - Irc_2;

        // Rimuove il ";" finale, se presente
        boolean hasSemicolon = rhs.endsWith(";");
        if (hasSemicolon) {
            rhs = rhs.substring(0, rhs.length() - 1);
        }

        // Estrai i termini separati da "-" e normalizzali
        List<String> terms = Arrays.stream(rhs.split("-"))
                .map(String::trim)
                .filter(t -> t.startsWith("Irc_"))
                .collect(Collectors.toList());

        if (terms.isEmpty()) return line;

        Collections.sort(terms);

        String normalized = lhs + "=" + String.join("-", terms);
        if (hasSemicolon) normalized += ";";

        return normalized;
    }

    static class TransitionStub {
        String source;
        String target;
        Map<String, String> labels;

        TransitionStub(String source, String target, Map<String, String> labels) {
            this.source = source;
            this.target = target;
            this.labels = labels;
        }

        boolean matches(TransitionStub other) {
            if (!this.source.equals(other.source)) return false;
            if (!this.target.equals(other.target)) return false;

            for (String kind : List.of("guard", "synchronisation", "assignment")) {
                String val1 = this.labels.get(kind);
                String val2 = other.labels.get(kind);
                if (!val1.isEmpty() && !val1.equals(val2)) return false;
            }
            return true;
        }
    }

    private List<String> extractOrConditions(String query) {
        int start = query.indexOf("(");
        int end = query.lastIndexOf(")");
        if (start == -1 || end == -1 || end <= start) {
            throw new IllegalArgumentException("Invalid query format: " + query);
        }

        String conditionBody = query.substring(start + 1, end);

        return Arrays.stream(conditionBody.split("\\|\\|"))
                .map(String::trim)
                .map(this::normalizeCondition)
                .collect(Collectors.toList());
    }

    private String normalizeCondition(String cond) {
        // Rimuove spazi superflui e ordina eventuali sotto-condizioni con &&
        // Esempio: !CB1.Open && CB5.Open && F1 => CB5.Open&&F1&&!CB1.Open
        List<String> parts = Arrays.stream(cond.split("&&"))
                .map(String::trim)
                .sorted()
                .collect(Collectors.toList());
        return String.join("&&", parts);
    }

}
