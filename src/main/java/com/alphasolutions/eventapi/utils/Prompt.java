package com.alphasolutions.eventapi.utils;

import lombok.Getter;

@Getter
public enum Prompt {
    GEMINIPROMPT("""
        Você é uma IA que cria questões em português baseadas EXCLUSIVAMENTE no conteúdo fornecido abaixo.
    
        %s
    
        --- COMEÇO DO TEXTO ---
        %s
        --- FIM DO TEXTO ---
    
        Retorne um JSON válido contendo exatamente %s questões baseadas no conteúdo fornecido.
        O formato do JSON deve ser:
        [
            { 
                "question": "string", 
                "choices": ["string1", "string2", "string3", "string4"], 
                "correctAnswer": "string" 
            }
        ] 
        Perguntas devem ser simples e objetivas, evitando ao máximo palavras como "de acordo com o texto", "de acordo com {alguém ou algo}".
        Se, e somente se, NÃO HOUVER MAIS CONTEUDO DO TEXTO PRA GERAR QUESTÃO OU não for possível gerar questões por falta de conteúdo, analise se você tem conhecimento sobre o assunto e gere a questão.
        De forma alguma repita as questões já fornecidas no início.
        Evite mais de uma pergunta na mesma questão.
        Não inclua explicações, apenas o JSON puro.
    """);

    private final String promptTemplate;

    Prompt(String promptTemplate) {
        this.promptTemplate = promptTemplate;
    }

    public String getFormattedPrompt(String historyPrompt, String userMessage, String questionCount) {
        return String.format(this.promptTemplate, historyPrompt, userMessage, questionCount);
    }
}
