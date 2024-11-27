/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.compilador;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

class Token {

    public enum TokenType {
        IDENTIFIER, INTEGER, OPERATOR, WHITESPACE
    }

    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Token[type=%s, value='%s']", type, value);
    }
}

public class Compilador {

    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "(?<IDENTIFIER>[a-zA-Z][a-zA-Z0-9]*)"
            + // Identificadores
            "|(?<INTEGER>[0-9]+)"
            + // Números inteiros
            "|(?<OPERATOR>[+\\-*/])"
            + // Operadores
            "|(?<WHITESPACE>\\s+)" // Espaços em branco
    );

    public static HashSet preencheAlfabeto(HashSet<String> a) {
        for (char i = 'A'; i <= 'Z'; i++) {
            a.add(String.valueOf(i));
        }
        for (char i = 'a'; i <= 'z'; i++) {
            a.add(String.valueOf(i));
        }
        for (char i = '0'; i <= '9'; i++) {
            a.add(String.valueOf(i));
        }
        char[] especiais = {'ç', 'á', 'é', 'í', 'ó', 'ú', 'ã', 'õ', 'ê', 'î', 'ô', 'û', 'à', 'è', 'ì', 'ò', 'ù', ',', '.', '!', '?', ':', ';', '(', ')', '-', '"', '\'', '/', '´', '`', '~', '^', '[', ']', '{', '}', '@', '#', '$', '%', '&', '*', '_', '=', '+', '|', '\\'};
        for (char i : especiais) {
            a.add(String.valueOf(i));
        }

        return a;
    }

    public static void alfabetoValido(String[] palavras, HashSet<String> alfabeto, List<String> lista) {
        for (String palavra : palavras) {
            boolean nPertence = false;
            for (char i : palavra.toCharArray()) {
                if (!alfabeto.contains(String.valueOf(i))) {
                    nPertence = true;
                    break;
                }
            }
            if (!nPertence) {
                lista.add(palavra);
            }
        }
    }

    public static List<String> stopwordValida(List<String> stopwords, List<String> palavras) {
        for (int i = 0; i < stopwords.size(); i++) {
            for (int j = 0; j < palavras.size(); j++) {
                if (stopwords.get(i).contains(palavras.get(j))) {
                    palavras.remove(j);
                }
            }
        }
        return palavras;
    }

    public static List<String> separarPontos(List<String> filaDePalavra) {
        List<String> r = new ArrayList<>();

        Pattern pattern = Pattern.compile("[ ,!?:;()-@]+|\\p{L}+\\w*(?=[\\p{L}+\\w*()^~-]*|$)");
        for (String fila : filaDePalavra) {
            Matcher matcher = pattern.matcher(fila);
            while (matcher.find()) {
                String palavraOuPontuacao = matcher.group();
                r.add(palavraOuPontuacao);
            }
        }
        filaDePalavra.clear();
        filaDePalavra.addAll(r);
        return filaDePalavra;
    }

    public static List<String> retirarPontuacao(List<String> tabela) {
        List<String> t = new ArrayList<>();

        Pattern pattern = Pattern.compile(" .|[\\p{L}\\d-]+");
        for (String table : tabela) {
            Matcher matcher = pattern.matcher(table);
            while (matcher.find()) {
                String palavraOuPontuacao = matcher.group();
                t.add(palavraOuPontuacao);
            }
        }
        tabela.clear();
        tabela.addAll(t);
        return tabela;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();

        List<String> stopwords = new ArrayList<>();

        LinkedList<String> filaDePalavras = new LinkedList<>();
        HashSet<String> alfabeto = new HashSet<>();
        String[] palavras = null;

        input = input.toLowerCase();
        String r = " ";
        palavras = input.split(r);

        preencheAlfabeto(alfabeto);
        String[] stpwords = {"a", "à", "adeus", "agora", "aí", "ainda", "além", "algo", "alguém", "algum", "alguma", "algumas", "alguns", "ali", "ampla", "amplas", "amplo", "amplos", "ano", "anos", "ante", "antes", "ao", "aos", "apenas", "apoio", "após", "aquela", "aquelas", "aquele", "aqueles", "aqui", "aquilo", "área", "as", "às", "assim", "até", "atrás", "através", "baixo", "bastante", "bem", "boa", "boas", "bom", "bons", "breve", "cá", "cada", "catorze", "cedo", "cento", "certamente", "certeza", "cima", "cinco", "coisa", "coisas", "com", "como", "conselho", "contra", "contudo", "custa", "da", "dá", "dão", "daquela", "daquelas", "daquele", "daqueles", "dar", "das", "de", "debaixo", "dela", "delas", "dele", "deles", "demais", "dentro", "depois", "desde", "dessa", "dessas", "desse", "desses", "desta", "destas", "deste", "destes", "deve", "devem", "devendo", "dever", "deverá", "deverão", "deveria", "deveriam", "devia", "deviam", "dez", "dezanove", "dezasseis", "dezassete", "dezoito", "dia", "diante", "disse", "disso", "disto", "dito", "diz", "dizem", "dizer", "do", "dois", "dos", "doze", "duas", "dúvida", "e", "é", "ela", "elas", "ele", "eles", "em", "embora", "enquanto", "entre", "era", "eram", "éramos", "és", "essa", "essas", "esse", "esses", "esta", "está", "estamos", "estão", "estar", "estas", "estás", "estava", "estavam", "estávamos", "este", "esteja", "estejam", "estejamos", "estes", "esteve", "estive", "estivemos", "estiver", "estivera", "estiveram", "estivéramos", "estiverem", "estivermos", "estivesse", "estivessem", "estivéssemos", "estiveste", "estivestes", "estou", "etc", "eu", "exemplo", "faço", "falta", "favor", "faz", "fazeis", "fazem", "fazemos", "fazendo", "fazer", "fazes", "feita", "feitas", "feito", "feitos", "fez", "fim", "final", "foi", "fomos", "for", "fora", "foram", "fôramos", "forem", "forma", "formos", "fosse", "fossem", "fôssemos", "foste", "fostes", "fui", "geral", "grande", "grandes", "grupo", "há", "haja", "hajam", "hajamos", "hão", "havemos", "havia", "hei", "hoje", "hora", "horas", "houve", "houvemos", "houver", "houvera", "houverá", "houveram", "houvéramos", "houverão", "houverei", "houverem", "houveremos", "houveria", "houveriam", "houveríamos", "houvermos", "houvesse", "houvessem", "houvéssemos", "isso", "isto", "já", "lá", "lado", "lhe", "lhes", "lo", "local", "logo", "longe", "lugar", "maior", "maioria", "mais", "mal", "mas", "máximo", "me", "meio", "menor", "menos", "mês", "meses", "mesma", "mesmas", "mesmo", "mesmos", "meu", "meus", "mil", "minha", "minhas", "momento", "muita", "muitas", "muito", "muitos", "na", "nada", "não", "naquela", "naquelas", "naquele", "naqueles", "nas", "nem", "nenhum", "nenhuma", "nessa", "nessas", "nesse", "nesses", "nesta", "nestas", "neste", "nestes", "ninguém", "nível", "no", "noite", "nome", "nos", "nós", "nossa", "nossas", "nosso", "nossos", "nova", "novas", "nove", "novo", "novos", "num", "numa", "número", "nunca", "o", "obra", "obrigada", "obrigado", "oitava", "oitavo", "oito", "onde", "ontem", "onze", "os", "ou", "outra", "outras", "outro", "outros", "para", "parece", "parte", "partir", "paucas", "pela", "pelas", "pelo", "pelos", "pequena", "pequenas", "pequeno", "pequenos", "per", "perante", "perto", "pode", "pude", "pôde", "podem", "podendo", "poder", "poderia", "poderiam", "podia", "podiam", "põe", "põem", "pois", "ponto", "pontos", "por", "porém", "porque", "porquê", "posição", "possível", "possivelmente", "posso", "pouca", "poucas", "pouco", "poucos", "primeira", "primeiras", "primeiro", "primeiros", "própria", "próprias", "próprio", "próprios", "próxima", "próximas", "próximo", "próximos", "pude", "puderam", "quais", "qual", "quando", "quanto", "quantos", "quarta", "quarto", "quatro", "que", "quê", "quem", "quer", "quereis", "querem", "queremas", "queres", "quero", "questão", "quinta", "quinto", "quinze", "relação", "sabe", "sabem", "são", "se", "segunda", "segundo", "sei", "seis", "seja", "sejam", "sejamos", "sem", "sempre", "sendo", "ser", "será", "serão", "serei", "seremos", "seria", "seriam", "seríamos", "sete", "sétima", "sétimo", "seu", "seus", "sexta", "sexto", "si", "sido", "sim", "sistema", "só", "sob", "sobre", "sois", "somos", "sou", "sua", "suas", "tal", "talvez", "também", "tampouco", "tanta", "tantas", "tanto", "tão", "tarde", "te", "tem", "tém", "têm", "temos", "tendes", "tendo", "tenha", "tenham", "tenhamos", "tenho", "tens", "ter", "terá", "terão", "terceira", "terceiro", "terei", "teremos", "teria", "teriam", "teríamos", "teu", "teus", "teve", "ti", "tido", "tinha", "tinham", "tínhamos", "tive", "tivemos", "tiver", "tivera", "tiveram", "tivéramos", "tiverem", "tivermos", "tivesse", "tivessem", "tivéssemos", "tiveste", "tivestes", "toda", "todavia", "todo", "todos", "trabalho", "três", "treze", "tu", "tua", "tuas", "tudo", "última", "últimas", "último", "últimos", "um", "uma", "umas", "uns", "vai", "vais", "vão", "vários", "vem", "vêm", "vendo", "vens", "ver", "vez", "vezes", "viagem", "vindo", "vinte", "vir", "você", "vocês", "vos", "vós", "vossa", "vossas", "vosso", "vossos", "zero", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "_", "acordo", "aí", "além", "alguém", "algum", "alguma", "algumas", "alguns", "ali", "alguma", "algo", "aqui", "assim", "atrás", "cada", "cá", "certamente", "cima", "com", "contra", "coisa", "comprido", "correr", "cortar", "cuja", "cujo", "daquela", "daquele", "debaixo", "depois", "desde", "desligado", "dessa", "desse", "desta", "deste", "debaixo", "detalhes", "devido", "dois", "ela", "elas", "ele", "eles", "embaixo", "entre", "então", "envolvido", "essa", "esse", "esta", "este", "etc", "exemplo", "fazer", "fica", "ficar", "fiz", "geral", "grande", "grandes", "havia", "hei", "hoje", "hora", "iniciar", "inicio", "início", "isto", "já", "lá", "logo", "longe", "maior", "maioria", "mais", "meio", "menos", "mesma", "mesmo", "meu", "minha", "muitas", "muitos", "nada", "não", "não", "nas", "nem", "nós", "neste", "ninguém", "noite", "nome", "onde", "ontem", "outra", "outras", "outro", "outros", "para", "pela", "pelo", "pequena", "pequeno", "pode", "podem", "poder", "poderia", "por favor", "porquê", "porque", "porquê", "posso", "próprio", "próximo", "quando", "quanto", "que", "quem", "se", "seu", "seus", "seja", "sejam", "sem", "sempre", "sendo", "ser", "sido", "só", "sob", "sobre", "somente", "tão", "também", "tem", "tempo", "têm", "tenho", "tentar", "tentaram", "tente", "tentou", "ter", "tentaram", "teve", "tive", "todos", "tudo", "um", "uma", "umas", "uns", "várias", "várias", "vários", "você", "vocês"};
        stopwords.addAll(Arrays.asList(stpwords));

        Matcher matcher = TOKEN_PATTERNS.matcher(input);

        while (matcher.find()) {
            if (matcher.group("IDENTIFIER") != null) {
                tokens.add(new Token(Token.TokenType.IDENTIFIER, matcher.group("IDENTIFIER")));
            } else if (matcher.group("INTEGER") != null) {
                tokens.add(new Token(Token.TokenType.INTEGER, matcher.group("INTEGER")));
            } else if (matcher.group("OPERATOR") != null) {
                tokens.add(new Token(Token.TokenType.OPERATOR, matcher.group("OPERATOR")));
            } else if (matcher.group("WHITESPACE") != null) {
                // Ignora espaços em branco
            }
        }
        return tokens;
    }

    public static void main(String[] args) {
        Compilador lexer = new Compilador();
        Scanner in = new Scanner(System.in);
        System.out.println("Digite seu texto: ");
        String input = in.nextLine();

        List<Token> tokens = lexer.tokenize(input);

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
