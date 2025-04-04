import java.util.*;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogador3;
    private Jogador jogador4;
    private Baralho baralho;
    private Carta vira;
    private String manilhaValor;
    private Scanner scanner;
    private int valorRodada;
    private Jogador ultimoQuePediuTruco;

    public Jogo() {
        jogador1 = new Jogador("Jogador 1");
        jogador2 = new Jogador("Jogador 2");
        jogador3 = new Jogador("Jogador 3");
        jogador4 = new Jogador("Jogador 4");
        baralho = new Baralho();
        scanner = new Scanner(System.in);
        valorRodada = 1;
    }

    public static void main(String[] args) {
        new Jogo().iniciar();
    }

    public void iniciar() {
        baralho.embaralhar();
        distribuirCartas();
        definirManilha();
        mostrarInformacoesIniciais();

        int vitoriasTimeA = 0;
        int vitoriasTimeB = 0;

        for (int rodada = 1; rodada <= 3; rodada++) {
            System.out.println("\n----------- Rodada " + rodada + " -----------");
            int vencedorRodada = jogarRodada();

            if (vencedorRodada == 1) {
                vitoriasTimeA++;
                System.out.println("Time A venceu a rodada!");
            } else if (vencedorRodada == 2) {
                vitoriasTimeB++;
                System.out.println("Time B venceu a rodada!");
            } else {
                System.out.println("Rodada empatada!");
            }

            if (vitoriasTimeA == 2 || vitoriasTimeB == 2) {
                break;
            }
        }

        anunciarVencedor(vitoriasTimeA, vitoriasTimeB);
    }

    private void distribuirCartas() {
        for (int i = 0; i < 3; i++) {
            jogador1.receberCarta(baralho.distribuirCarta());
            jogador2.receberCarta(baralho.distribuirCarta());
            jogador3.receberCarta(baralho.distribuirCarta());
            jogador4.receberCarta(baralho.distribuirCarta());
        }
        vira = baralho.distribuirCarta();
    }

    private void definirManilha() {
        String[] ordem = {"4", "5", "6", "7", "Q", "J", "K", "A", "2", "3"};
        for (int i = 0; i < ordem.length; i++) {
            if (ordem[i].equals(vira.getValor())) {
                manilhaValor = (i == ordem.length - 1) ? ordem[0] : ordem[i + 1];
                break;
            }
        }
    }

    private void mostrarInformacoesIniciais() {
        System.out.println("\n========================================");
        System.out.println("          JOGO DE TRUCO - DUPLAS         ");
        System.out.println("========================================");
        System.out.println("Carta Vira: " + vira);
        System.out.println("Manilha: " + manilhaValor);
        System.out.println("========================================\n");
    }

    private int jogarRodada() {
        valorRodada = 1; // Cada rodada começa valendo 1 ponto
        ultimoQuePediuTruco = null;

        Map<Jogador, Carta> cartasJogadas = new LinkedHashMap<>();

        cartasJogadas.put(jogador1, escolherCartaComTruco(jogador1, jogador2));
        cartasJogadas.put(jogador2, escolherCartaComTruco(jogador2, jogador3));
        cartasJogadas.put(jogador3, escolherCartaComTruco(jogador3, jogador4));
        cartasJogadas.put(jogador4, escolherCartaComTruco(jogador4, jogador1));

        System.out.println("\nCartas Jogadas:");
        cartasJogadas.forEach((jogador, carta) -> System.out.println(jogador.getNome() + ": " + carta));

        return determinarVencedorDuplas(cartasJogadas);
    }

    private Carta escolherCartaComTruco(Jogador jogadorAtual, Jogador oponente) {
        System.out.println(jogadorAtual.getNome() + " - Suas cartas:");
        for (int i = 0; i < jogadorAtual.getMao().size(); i++) {
            System.out.println((i + 1) + ": " + jogadorAtual.getMao().get(i));
        }

        if (valorRodada < 12 && jogadorAtual != ultimoQuePediuTruco) {
            if (valorRodada == 1) {
                System.out.print("Deseja pedir TRUCO? (S/N): ");
            } else {
                System.out.print("Deseja aumentar o TRUCO? (S/N): ");
            }

            String resposta = scanner.next().trim().toUpperCase();

            if (resposta.equals("S")) {
                if (!resolverTruco(jogadorAtual, oponente)) {
                    System.out.println(oponente.getNome() + " correu! " + jogadorAtual.getNome() + " ganhou a rodada!");
                    if (timeDoJogador(jogadorAtual) == 1) {
                        jogador1.adicionarPonto();
                        jogador3.adicionarPonto();
                    } else {
                        jogador2.adicionarPonto();
                        jogador4.adicionarPonto();
                    }
                    anunciarPlacarFinal();
                    System.exit(0);
                }
                ultimoQuePediuTruco = jogadorAtual;
            }
        }

        int escolha;
        while (true) {
            System.out.print("Escolha a carta para jogar (1-" + jogadorAtual.getMao().size() + "): ");
            if (scanner.hasNextInt()) {
                escolha = scanner.nextInt();
                if (escolha >= 1 && escolha <= jogadorAtual.getMao().size()) {
                    break;
                }
            }
            System.out.println("Escolha inválida. Tente novamente.");
            scanner.nextLine();
        }

        return jogadorAtual.jogarCarta(escolha - 1);
    }

    private boolean resolverTruco(Jogador quemPediu, Jogador quemResponde) {
        System.out.println(quemResponde.getNome() + ", " + quemPediu.getNome() + " pediu TRUCO!");

        if (valorRodada < 12) {
            System.out.println("1 - Correr (perder a rodada)");
            System.out.println("2 - Aceitar (rodada vale " + (valorRodada == 1 ? 3 : valorRodada) + " pontos)");
            System.out.println("3 - Aumentar (" + proximoValorTruco() + " pontos)");
        } else {
            System.out.println("1 - Correr (perder a rodada)");
            System.out.println("2 - Aceitar (rodada vale 12 pontos)");
        }

        int resposta;
        while (true) {
            System.out.print("Escolha (1-" + (valorRodada < 12 ? "3" : "2") + "): ");
            if (scanner.hasNextInt()) {
                resposta = scanner.nextInt();
                if ((valorRodada < 12 && resposta >= 1 && resposta <= 3) || (valorRodada == 12 && (resposta == 1 || resposta == 2))) {
                    break;
                }
            }
            System.out.println("Escolha inválida. Tente novamente.");
            scanner.nextLine();
        }

        if (resposta == 1) {
            return false; // Correr
        } else if (resposta == 2) {
            if (valorRodada == 1) {
                valorRodada = 3;
            }
            return true; // Aceitar
        } else { // resposta == 3
            valorRodada = proximoValorTruco();
            return resolverTruco(quemResponde, quemPediu);
        }
    }

    private int proximoValorTruco() {
        if (valorRodada == 1) return 3;
        if (valorRodada == 3) return 6;
        if (valorRodada == 6) return 9;
        return 12;
    }

    private int determinarVencedorDuplas(Map<Jogador, Carta> cartasJogadas) {
        Jogador vencedor = cartasJogadas.entrySet()
            .stream()
            .max(Comparator.comparingInt(entry -> calcularPoder(entry.getValue())))
            .get()
            .getKey();

        int time = timeDoJogador(vencedor);
        return time;
    }

    private int calcularPoder(Carta carta) {
        Map<String, Integer> ordem = new HashMap<>();
        ordem.put("4", 1);
        ordem.put("5", 2);
        ordem.put("6", 3);
        ordem.put("7", 4);
        ordem.put("Q", 5);
        ordem.put("J", 6);
        ordem.put("K", 7);
        ordem.put("A", 8);
        ordem.put("2", 9);
        ordem.put("3", 10);

        if (carta.getValor().equals(manilhaValor)) {
            switch (carta.getNaipe()) {
                case "Ouros": return 11;
                case "Espadas": return 12;
                case "Copas": return 13;
                case "Paus": return 14;
            }
        }
        return ordem.getOrDefault(carta.getValor(), 0);
    }

    private int timeDoJogador(Jogador jogador) {
        if (jogador == jogador1 || jogador == jogador3) {
            return 1; // Time A
        } else {
            return 2; // Time B
        }
    }

    private void anunciarVencedor(int vitoriasTimeA, int vitoriasTimeB) {
        System.out.println("\n========================================");
        if (vitoriasTimeA > vitoriasTimeB) {
            System.out.println("Time A venceu o jogo!");
        } else if (vitoriasTimeB > vitoriasTimeA) {
            System.out.println("Time B venceu o jogo!");
        } else {
            System.out.println("O jogo terminou empatado!");
        }
        anunciarPlacarFinal();
    }

    private void anunciarPlacarFinal() {
        System.out.println("========================================\n");
        System.out.println("Placar Final:");
        System.out.println("Jogador 1: " + jogador1.getPontos() + " ponto(s)");
        System.out.println("Jogador 2: " + jogador2.getPontos() + " ponto(s)");
        System.out.println("Jogador 3: " + jogador3.getPontos() + " ponto(s)");
        System.out.println("Jogador 4: " + jogador4.getPontos() + " ponto(s)");
    }
}