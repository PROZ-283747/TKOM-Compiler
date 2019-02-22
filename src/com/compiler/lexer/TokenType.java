package com.compiler.lexer;

public enum TokenType {

    // Single character tokens
    LEFT_PARENTHESIS,       //"{"
    RIGHT_PARENTHESIS,      //"}"
    LEFT_BRACKET,           //"("
    RIGHT_BRACKET,          //")"
    LEFT_SQUARE_BRACKET,    //"["
    RIGHT_SQUARE_BRACKET,   //"]"
    COMMA,                  //","
    COLON,                  //":"
    SEMICOLON,              //";"
    APOSTROPHE,             //"'"
    SLASH,                  //"/"
    STAR,                   //"*"
    HASH,                   //"#"

    // One or two characters tokens
    PLUS,               //"+"
    INCREMENTATION,     //"++"
    MINUS,              //"-"
    DECREMENTATION,     //"--"
    MINUS_EQUAL,        //"-="
    PLUS_EQUAL,         //"+="
    BANG_EQUAL,         //"!="
    ASSIGNMENT,         //"="
    EQUAL,              //"=="
    GREATER,            //">"
    GREATER_EQUAL,      //">="
    LESS,               //"<"
    LESS_EQUAL,         //"<="
    AND,                //"&&"
    OR,                 //"||"

    // Keywords
    FRACTION_T,         //"fraction"
    STRING_T,           // "string"
    VOID_T,             // "void"
    ELSE,               // "else"
    FOR,                // "for"
    IF,                 // "if"
    WHILE,              // "while"
    RETURN,             // "return"

    // Literals
    IDENTIFIER,         // eg. "myVariable"
    STRING,             // eg. "'Example of string'"
    NUMBER,             // eg. 5%7, 18%1, 45

    // End of file
    END_OF_FILE,        // EOF

    // When error
    ERROR               // sth incorrect
}
