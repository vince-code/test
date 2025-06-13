package polimi.logic.generator.builder.declarations;

import polimi.logic.generator.builder.declarations.functions.ClearFunctionBuilder;
import polimi.logic.generator.builder.declarations.functions.IscFunctionBuilder;
import polimi.logic.generator.builder.declarations.functions.UpdateFunctionBuilder;
import polimi.model.*;
import polimi.util.IndentedStringBuilder;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalDeclarationsBuilder {

    private final GlobalVariablesBuilder variablesBuilder;
    private final IscFunctionBuilder iscBuilder;
    private final UpdateFunctionBuilder updateBuilder;
    private final ClearFunctionBuilder clearBuilder;

    public GlobalDeclarationsBuilder(Network network) {
        variablesBuilder = new GlobalVariablesBuilder(network);
        iscBuilder = new IscFunctionBuilder(network);
        updateBuilder = new UpdateFunctionBuilder(network);
        clearBuilder = new ClearFunctionBuilder(network);
    }

    public String build(){
        IndentedStringBuilder declarations = new IndentedStringBuilder();

        declarations
                .newLine()
                .append(variablesBuilder.build()).newLine()
                .append(iscBuilder.build()).newLine()
                .append(updateBuilder.build()).newLine()
                .append(clearBuilder.build()).newLine();

        return declarations.toString();
    }









}
