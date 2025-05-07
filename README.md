


# EventAPI - Guia de Execução

## Requisitos
Certifique-se de ter os seguintes requisitos instalados antes de executar o projeto:
- [Java 17+](https://adoptium.net/)
- [Apache Maven 3+](https://maven.apache.org/)

## Compilando e Empacotando a Aplicação
No diretório raiz do projeto (`eventAPI`), execute o seguinte comando para limpar e empacotar a aplicação:

```sh
mvn clean package
```

Esse comando irá compilar o código-fonte, rodar os testes e gerar um arquivo `.jar` na pasta `target`.

## Executando a Aplicação
Depois que o empacotamento for concluído, navegue até o diretório `target`:

```sh
cd target
```

Agora, execute o seguinte comando para iniciar a aplicação:

```sh
java -jar nome-do-arquivo.jar
```

Caso o nome do arquivo gerado seja desconhecido, você pode utilizar o seguinte comando para executar qualquer `.jar` presente:

```sh
java -jar *.jar
```

## Informações Adicionais
- Para interromper a execução, pressione `Ctrl + C`.
- Caso precise modificar configurações, edite o arquivo `application.properties` ou `application.yml`.
- Verifique os logs gerados para depuração e monitoramento.

Se houver dúvidas, consulte a documentação oficial do Maven e do Java para mais detalhes.

