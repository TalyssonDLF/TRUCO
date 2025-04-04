import java.util.*;

public class Jogo {
    private Jogador jogador1;
    private Jogador jogador2;
    private Baralho baralho;
    private Carta vira;
    private String manilhaValor;
    private Scanner scanner;
    private int valorRodada;

    public Jogo() {
        jogador1 = new Jogador("Jogador 1");
        jogador2 = new Jogador("Jogador 2");
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

        int vitoriasJogador1 = 0;
        int vitoriasJogador2 = 0;

        for (int rodada = 1; rodada <= 3; rodada++) {
            System.out.println("\n----------- Rodada " + rodada + " -----------");
            int vencedorRodada = jogarRodada();

            if (vencedorRodada == 1) {
                vitoriasJogador1++;
                System.out.println(jogador1.getNome() + " venceu a rodada!");
            } else if (vencedorRodada == 2) {
                vitoriasJogador2++;
                System.out.println(jogador2.getNome() + " venceu a rodada!");
            } else {
                System.out.println("Rodada empatada!");
            }

            if (vitoriasJogador1 == 2 || vitoriasJogador2 == 2) {
                break;
            }
        }

        anunciarVencedor(vitoriasJogador1, vitoriasJogador2);
    }

    private void distribuirCartas() {
        for (int i = 0; i < 3; i++) {
            jogador1.receberCarta(baralho.distribuirCarta());
            jogador2.receberCarta(baralho.distribuirCarta());
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
        System.out.println("          JOGO DE TRUCO - INÍCIO         ");
        System.out.println("========================================");
        System.out.println("Carta Vira: " + vira);
        System.out.println("Manilha: " + manilhaValor);
        System.out.println("========================================\n");
    }

    private int jogarRodada() {
        valorRodada = 1; // A cada rodada começa valendo 1 ponto

        Carta carta1 = escolherCartaComTruco(jogador1, jogador2);
        Carta carta2 = escolherCartaComTruco(jogador2, jogador1);

        System.out.println("\n" + jogador1.getNome() + " jogou: " + carta1);
        System.out.println(jogador2.getNome() + " jogou: " + carta2);

        return determinarVencedor(carta1, carta2);
    }

    private Carta escolherCartaComTruco(Jogador jogadorAtual, Jogador oponente) {
        System.out.println(jogadorAtual.getNome() + " - Suas cartas:");
        for (int i = 0; i < jogadorAtual.getMao().size(); i++) {
            System.out.println((i + 1) + ": " + jogadorAtual.getMao().get(i));
        }

        if (valorRodada < 12) {
            if (valorRodada == 1) {
                System.out.print("Deseja pedir TRUCO? (S/N): ");
            } else {
                System.out.print("Deseja aumentar o TRUCO? (S/N): ");
            }

            String resposta = scanner.next().trim().toUpperCase();

            if (resposta.equals("S")) {
                if (!resolverTruco(jogadorAtual, oponente)) {
                    // Se o oponente correr, quem pediu ganha
                    System.out.println(oponente.getNome() + " correu! " + jogadorAtual.getNome() + " ganhou a rodada!");
                    if (jogadorAtual == jogador1) {
                        jogador1.adicionarPonto();
                    } else {
                        jogador2.adicionarPonto();
                    }
                    anunciarPlacarFinal();
                    System.exit(0);
                }
            }
        }

        int escolha = -1;
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

        int resposta = -1;
        while (true) {
            System.out.print("Escolha (1-" + (valorRodada < 12 ? "3" : "2") + "): ");
            if (scanner.hasNextInt()) {
                resposta = scanner.nextInt();
                if ((valorRodada < 12 && (resposta >= 1 && resposta <= 3)) || (valorRodada == 12 && (resposta == 1 || resposta == 2))) {
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

    private int determinarVencedor(Carta carta1, Carta carta2) {
        int poder1 = calcularPoder(carta1);
        int poder2 = calcularPoder(carta2);

        if (poder1 > poder2) {
            return 1;
        } else if (poder2 > poder1) {
            return 2;
        } else {
            return 0; // empate
        }
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

    private void anunciarVencedor(int vitoriasJogador1, int vitoriasJogador2) {
        System.out.println("\n========================================");
        if (vitoriasJogador1 > vitoriasJogador2) {
            jogador1.adicionarPonto();
            System.out.println(jogador1.getNome() + " venceu o jogo!");
        } else if (vitoriasJogador2 > vitoriasJogador1) {
            jogador2.adicionarPonto();
            System.out.println(jogador2.getNome() + " venceu o jogo!");
        } else {
            System.out.println("O jogo terminou empatado!");
        }
        anunciarPlacarFinal();
    }

    private void anunciarPlacarFinal() {
        System.out.println("========================================\n");
        System.out.println("Placar Final:");
        System.out.println(jogador1.getNome() + ": " + jogador1.getPontos() + " ponto(s)");
        System.out.println(jogador2.getNome() + ": " + jogador2.getPontos() + " ponto(s)");
    }
}