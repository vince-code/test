package polimi.logic.modelGenerator.builder.declarations;

import polimi.logic.modelGenerator.builder.declarations.functions.ClearFunctionBuilder;
import polimi.logic.modelGenerator.builder.declarations.functions.IscFunctionBuilder;
import polimi.logic.modelGenerator.builder.declarations.functions.UpdateFunctionBuilder;
import polimi.model.*;
import polimi.logic.modelGenerator.IndentedStringBuilder;

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
