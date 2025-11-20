
package TabelaDeSimbolos;

import java.util.*;

import Tipo_de_dados.TipoDado;

public class TabelaDeSimbolos {
    private final Deque<Map<String, TipoDado>> pilha = new ArrayDeque<>();

    public TabelaDeSimbolos() {
        abrirEscopo(); // escopo global
    }

    public void abrirEscopo() {
        pilha.push(new HashMap<>());
    }

    public void fecharEscopo() {
        pilha.pop();
    }

    public boolean declarar(String nome, TipoDado tipo) {
        Map<String, TipoDado> topo = pilha.peek();
        if (topo.containsKey(nome)) return false; // já declarado neste escopo
        topo.put(nome, tipo);
        return true;
    }

    public TipoDado buscar(String nome) {
        for (Map<String, TipoDado> escopo : pilha) {
            if (escopo.containsKey(nome)) return escopo.get(nome);
        }
        return null;
    }
}
