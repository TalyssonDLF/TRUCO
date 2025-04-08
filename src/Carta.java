import java.util.Map;

public class Carta {
    private String valor;
    private String naipe;

    public Carta(String valor, String naipe) {
        this.valor = valor;
        this.naipe = naipe;
    }

    public String getValor() {
        return valor;
    }

    public String getNaipe() {
        return naipe;
    }

    public int calcularPoder(String manilhaValor) {
        
        Map<String, Integer> ordem = Map.of(
            "4", 1, "5", 2, "6", 3, "7", 4,
            "Q", 5, "J", 6, "K", 7, "A", 8, "2", 9, "3", 10
        );

        
        if (this.valor.equals(manilhaValor)) {
            switch (this.naipe) {
                case "Ouros": return 11;
                case "Espadas": return 12;
                case "Copas": return 13;
                case "Paus": return 14;
            }
        }

        return ordem.getOrDefault(this.valor, 0);
    }

    @Override
    public String toString() {
        return valor + " de " + naipe;
    }
}