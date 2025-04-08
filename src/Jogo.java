import java.util.*;

public class Jogo {
    private Jogador jogador1, jogador2, jogador3, jogador4;
    private Baralho baralho;
    private Carta vira;
    private String manilhaValor;
    private Scanner scanner;
    private int valorRodada;
    private Jogador ultimoQuePediuTruco;
    private List<Jogador> ordemJogadores;
    private int pontosTimeA = 0, pontosTimeB = 0;

    public Jogo() {
        jogador1 = new Jogador("Jogador 1");
        jogador2 = new Jogador("Jogador 2");
        jogador3 = new Jogador("Jogador 3");
        jogador4 = new Jogador("Jogador 4");
        baralho = new Baralho();
        scanner = new Scanner(System.in);
        valorRodada = 1;
        ordemJogadores = new ArrayList<>(Arrays.asList(jogador1, jogador2, jogador3, jogador4));
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
            System.out.println("\n=========== Rodada " + rodada + " ===========");
            Jogador vencedorRodada = jogarRodada();
            int timeVencedor = timeDoJogador(vencedorRodada);
            if (timeVencedor == 1) {
                vitoriasTimeA++;
                System.out.println(">>> Time A venceu a rodada!");
            } else if (timeVencedor == 2) {
                vitoriasTimeB++;
                System.out.println(">>> Time B venceu a rodada!");
            } else {
                System.out.println(">>> Rodada empatada!");
            }
            atualizarOrdem(vencedorRodada);
            if (vitoriasTimeA == 2 || vitoriasTimeB == 2) break;
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

    private Jogador jogarRodada() {
        valorRodada = 1;
        ultimoQuePediuTruco = null;
        Map<Jogador, Carta> cartasJogadas = new LinkedHashMap<>();

        for (int i = 0; i < ordemJogadores.size(); i++) {
            Jogador jogadorAtual = ordemJogadores.get(i);
            Jogador oponente = ordemJogadores.get((i + 1) % ordemJogadores.size());
            cartasJogadas.put(jogadorAtual, escolherCartaComTruco(jogadorAtual, oponente));
        }

        System.out.println("\n----------- Cartas jogadas nesta rodada -----------");
        cartasJogadas.forEach((jogador, carta) ->
            System.out.println(jogador.getNome() + " jogou: " + carta)
        );
        System.out.println("---------------------------------------------------");

        return determinarVencedorDuplas(cartasJogadas);
    }

    private void atualizarOrdem(Jogador vencedor) {
        int index = ordemJogadores.indexOf(vencedor);
        if (index > 0) {
            Collections.rotate(ordemJogadores, -index);
        }
        System.out.println("\n>>> Nova ordem de jogada para a próxima rodada:");
        ordemJogadores.forEach(jogador -> System.out.print(jogador.getNome() + " "));
        System.out.println();
    }

    private Carta escolherCartaComTruco(Jogador jogadorAtual, Jogador oponente) {
        System.out.println("\n========================================");
        System.out.println(jogadorAtual.getNome() + " - Suas cartas:");
        for (int i = 0; i < jogadorAtual.getMao().size(); i++) {
            System.out.println((i + 1) + ": " + jogadorAtual.getMao().get(i));
        }
        System.out.println("========================================");

        if (valorRodada < 12 && jogadorAtual != ultimoQuePediuTruco) {
            String pergunta = (valorRodada == 1 ? "Deseja pedir TRUCO? (S/N): " : "Deseja aumentar o TRUCO? (S/N): ");
            System.out.print(pergunta);
            String resposta = scanner.next().trim().toUpperCase();

            if (resposta.equals("S")) {
                if (!resolverTruco(jogadorAtual, oponente)) {
                    System.out.println(oponente.getNome() + " correu! " + jogadorAtual.getNome() + " ganhou a rodada!");
                    if (timeDoJogador(jogadorAtual) == 1)
                        pontosTimeA += valorRodada;
                    else
                        pontosTimeB += valorRodada;
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

        Carta cartaJogada = jogadorAtual.jogarCarta(escolha - 1);
        System.out.println(">>> " + jogadorAtual.getNome() + " jogou: " + cartaJogada + "\n");
        return cartaJogada;
    }

    private boolean resolverTruco(Jogador quemPediu, Jogador quemResponde) {
        System.out.println("\n" + quemResponde.getNome() + ", " + quemPediu.getNome() + " pediu TRUCO!");

        System.out.println("\n========================================");
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Correr (perder a rodada)");
        System.out.println("2 - Aceitar (rodada vale " + (valorRodada == 1 ? 3 : valorRodada) + " pontos)");
        if (valorRodada < 12) System.out.println("3 - Aumentar (" + proximoValorTruco() + " pontos)");
        System.out.println("========================================");

        int resposta;
        while (true) {
            System.out.print("Escolha (1-" + (valorRodada < 12 ? "3" : "2") + "): ");
            if (scanner.hasNextInt()) {
                resposta = scanner.nextInt();
                if ((valorRodada < 12 && resposta >= 1 && resposta <= 3)
                        || (valorRodada == 12 && (resposta == 1 || resposta == 2))) {
                    break;
                }
            }
            System.out.println("Escolha inválida. Tente novamente.");
            scanner.nextLine();
        }

        if (resposta == 1) return false;
        else if (resposta == 2) {
            if (valorRodada == 1)
                valorRodada = 3;
            return true;
        } else {
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

    private Jogador determinarVencedorDuplas(Map<Jogador, Carta> cartasJogadas) {
        Jogador vencedor = cartasJogadas.entrySet()
            .stream()
            .max(Comparator.comparingInt(entry -> entry.getValue().calcularPoder(manilhaValor)))
            .get()
            .getKey();
        return vencedor;
    }

    private int timeDoJogador(Jogador jogador) {
        return (jogador == jogador1 || jogador == jogador3) ? 1 : 2;
    }

    private void anunciarVencedor(int vitoriasTimeA, int vitoriasTimeB) {
        System.out.println("\n========================================");
        if (vitoriasTimeA > vitoriasTimeB) {
            System.out.println(">>> Time A venceu o jogo!");
        } else if (vitoriasTimeB > vitoriasTimeA) {
            System.out.println(">>> Time B venceu o jogo!");
        } else {
            System.out.println(">>> O jogo terminou empatado!");
        }
        anunciarPlacarFinal();
    }

    private void anunciarPlacarFinal() {
        System.out.println("========================================\n");
        System.out.println("Placar Final:");
        System.out.println("Time A: " + pontosTimeA + " ponto(s)");
        System.out.println("Time B: " + pontosTimeB + " ponto(s)");
        System.out.println("========================================");
    }
}