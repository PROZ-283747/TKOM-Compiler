package com.compiler.lexer;

public enum TokenType {

    // Single character tokens
    LEFT_PAREN,              //"("
    RIGHT_PAREN,             //")"
    LEFT_BRACKET,           //"{"
    RIGHT_BRACKET,          //"}"
    LEFT_SQUARE_BRACKET,    //"["
    RIGHT_SQUARE_BRACKET,   //"]"
    COMMA,                  //","
    COLON,                  //":"
    SEMICOLON,              //";"
    APOSTROPHE,             //"'"
    SLASH,                  //"/"
    STAR,                   //"*"
    DOT,                    //"."

    // One or two characters tokens
    PLUS,               //"+"
    PLUS_PLUS,          //"++"
    MINUS,              //"-"
    MINUS_MINUS,        //"--"
    MINUS_EQUAL,        //"-="
    PLUS_EQUAL,         //"+="
    BANG,               //!
    BANG_EQUAL,         //"!="
    EQUAL,              //"="
    EQUAL_EQUAL,        //"=="
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
    CLASS,              // "class"
    FALSE,              // "false"
    TRUE,               // "true"
    PRINT,              // "print"
    NULL,               // "null"
    NEW,                // "new"
    OBJECT,             // "object"

    // Literals
    IDENTIFIER,         // eg. "myVariable"
    STRING,             // eg. "'Example of string'"
    FRACTION,             // eg. 5%7, 18%1, 45

    // End of file
    EOF,                 // EOF

    // When error
    ERROR               // sth incorrect
}
