package br.com.rotavital;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Garante a regra de dependencia lendo os imports do codigo-fonte:
 * dominio e aplicacao nao conhecem framework, banco, dados nem apresentacao;
 * contextos so se enxergam pela camada anticorrupcao (dados).
 */
class ArquiteturaEmCamadasTest {

    private static final Path RAIZ = Path.of("src/main/java/br/com/rotavital");
    private static final List<String> CONTEXTOS =
            List.of("alocacao", "requisicoes", "estoque", "expedicao", "identidade");
    private static final Pattern IMPORT = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.]+)", Pattern.MULTILINE);

    private static List<String> imports(Path arquivo) throws IOException {
        Matcher m = IMPORT.matcher(Files.readString(arquivo));
        return m.results().map(r -> r.group(1)).toList();
    }

    private static List<Path> arquivos(String contexto, String camada) throws IOException {
        Path base = RAIZ.resolve(contexto);
        try (Stream<Path> s = Files.walk(base)) {
            return s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> p.toString().replace('\\', '/').contains("/" + camada + "/"))
                    .toList();
        }
    }

    @Test
    void dominioNaoDependeDeFrameworkBancoNemDeOutrasCamadas() throws IOException {
        for (String ctx : CONTEXTOS) {
            for (Path arq : arquivos(ctx, "dominio")) {
                for (String imp : imports(arq)) {
                    assertThat(imp).as("%s importa %s", arq, imp)
                            .doesNotStartWith("org.springframework")
                            .doesNotStartWith("jakarta.")
                            .doesNotStartWith("java.sql")
                            .doesNotContain(".dados.")
                            .doesNotContain(".apresentacao.")
                            .doesNotContain(".aplicacao.");
                }
            }
        }
    }

    @Test
    void aplicacaoNaoDependeDeFrameworkDadosNemApresentacao() throws IOException {
        for (String ctx : CONTEXTOS) {
            for (Path arq : arquivos(ctx, "aplicacao")) {
                for (String imp : imports(arq)) {
                    assertThat(imp).as("%s importa %s", arq, imp)
                            .doesNotStartWith("org.springframework")
                            .doesNotStartWith("java.sql")
                            .doesNotContain(".dados.")
                            .doesNotContain(".apresentacao.");
                }
            }
        }
    }

    @Test
    void negocioDeUmContextoNaoImportaOutroContexto() throws IOException {
        for (String ctx : CONTEXTOS) {
            for (String camada : List.of("dominio", "aplicacao")) {
                for (Path arq : arquivos(ctx, camada)) {
                    for (String imp : imports(arq)) {
                        for (String outro : CONTEXTOS) {
                            if (!outro.equals(ctx)) {
                                assertThat(imp).as("%s importa %s", arq, imp)
                                        .doesNotStartWith("br.com.rotavital." + outro + ".");
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void apresentacaoNaoAcessaDadosDiretamente() throws IOException {
        for (String ctx : CONTEXTOS) {
            for (Path arq : arquivos(ctx, "apresentacao")) {
                for (String imp : imports(arq)) {
                    assertThat(imp).as("%s importa %s", arq, imp).doesNotContain(".dados.");
                }
            }
        }
    }
}
