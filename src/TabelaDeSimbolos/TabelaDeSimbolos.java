package TabelaDeSimbolos;

import Tipo_de_dados.TipoDado;
import java.util.*;

public class TabelaDeSimbolos {

    private final List<Map<String, TipoDado>> escopos = new ArrayList<>();

    public TabelaDeSimbolos() { abrirEscopo(); }

    public void abrirEscopo() { escopos.add(new HashMap<>()); }

    public void fecharEscopo() {
        if (escopos.size() > 1)
            escopos.remove(escopos.size() - 1);
    }

    public boolean declarar(String id, TipoDado tipo) {
        Map<String, TipoDado> atual = escopos.get(escopos.size() - 1);
        if (atual.containsKey(id)) return false;
        atual.put(id, tipo);
        return true;
    }

    public TipoDado buscar(String id) {
        for (int i = escopos.size() - 1; i >= 0; i--)
            if (escopos.get(i).containsKey(id))
                return escopos.get(i).get(id);
        return null;
    }
}
