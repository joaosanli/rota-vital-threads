package br.com.rotavital.identidade.dados;

import br.com.rotavital.identidade.negocio.dominio.Papel;
import br.com.rotavital.identidade.negocio.dominio.ProvedorDeIdentidade;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Cliente do provedor de identidade. Nesta etapa e um substituto sintetico: mapeia sujeitos
 * de teste para papeis. A integracao OIDC/JWT real entra na troca desta classe.
 */
@Component
public class ProvedorOidcClient implements ProvedorDeIdentidade {

    private static final Map<String, Papel> SUJEITOS = Map.of(
            "operador", Papel.OPERADOR,
            "coordenador", Papel.COORDENADOR,
            "admin", Papel.ADMINISTRADOR);

    @Override
    public Optional<Papel> papelDe(String sujeito) {
        return Optional.ofNullable(SUJEITOS.get(sujeito));
    }
}
