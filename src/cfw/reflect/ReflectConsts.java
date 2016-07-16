package cfw.reflect;

/**
 * Created by Cfw on 2016/7/16.
 */
public class ReflectConsts {
    private static final String [] General_Identifier_Simple_Names = {
            "Short","int","Integer","Long",
            "String","Boolean","Date","Double",
            "float","Float","double","byte","char"
    };

    private static final String [] Is_Identifier_Names = {"Boolean"};

    private static final boolean contains(String [] container, String content){
        for(String contentInContainer : container){
            if(contentInContainer.equalsIgnoreCase(content))
                return true;
            else
                continue;
        }

        return false;
    }

    public static final boolean generalIdentifierSimpleNamesContains(String identifierName){
       return contains(General_Identifier_Simple_Names,identifierName);
    }

    public static final boolean isIdentifierSimpleNamesContains(String identifierName){
       return contains(Is_Identifier_Names,identifierName);
    }


}
