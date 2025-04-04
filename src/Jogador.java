import java.util.ArrayList;
import java.util.List;

public class Jogador {
    private String nome;
    private List<Carta> mao;
    private int pontos;

    public Jogador(String nome) {
        this.nome = nome;
        this.mao = new ArrayList<>();
        this.pontos = 0;
    }

    public void receberCarta(Carta carta) {
        mao.add(carta);
    }

    public Carta jogarCarta(int indice) {
        return mao.remove(indice);
    }

    public List<Carta> getMao() {
        return mao;
    }

    public int getPontos() {
        return pontos;
    }

    public void adicionarPonto() {
        pontos++;
    }

    public String getNome() {
        return nome;
    }
}