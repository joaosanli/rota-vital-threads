# Rota Vital — Threads II (Projeto Integrador)

**Equipe:** João Lucas Santos Lira · Mariana Ferreira · Thaissa Fernandes · Priscila Maciel de Lima
**Professor:** Raoni Monteiro de Oliveira

Operação escolhida: **cruzamento requisições × estoque** — para cada requisição de insumo
pendente no país, sugerir o hospital fornecedor mais adequado (distância geodésica + regra de
estoque) e agregar indicadores nacionais (atendíveis, distância média, % no SLA por prioridade,
interestaduais, por UF).

- Big-O sequencial: **O(R·H)** (R requisições × H hospitais candidatos)
- Gargalo: **CPU** (centenas de milhões de cálculos trigonométricos), não o banco
- Particionável: cada requisição só lê dados imutáveis → fatias independentes + soma no fim

## Stack
Java 21 · Spring Boot 3.5 · Maven (wrapper incluso) · scripts de medição em Python 3.

## Rodar o servidor
```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
# ou abra o projeto na IDE e rode RotaVitalApplication (JDK 21)
```

## Endpoints
| Método | Rota | O que faz |
|---|---|---|
| POST | `/api/dados/gerar?requisicoes=1000000` | gera/carrega a base (semente fixa 42, 1.000 hospitais, 30 insumos) |
| GET | `/api/cruzamento?modo=sequencial&requisicoes=100000` | versão sequencial |
| GET | `/api/cruzamento?modo=plataforma&threads=4&requisicoes=1000000` | ExecutorService com N threads de plataforma |
| GET | `/api/cruzamento?modo=virtual&threads=8&requisicoes=1000000` | virtual threads do Java 21 (opcional da atividade) |
| GET | `/api/cruzamento?modo=inseguro&threads=8&requisicoes=100000` | **demo** de race condition (acumulador compartilhado sem lock) |
| GET | `/api/info` | núcleos, versão do Java, heap — ambiente da medição |

Todas as versões corretas devolvem a mesma `assinatura` (hash de todos os números da resposta).

## Medir e gerar o relatório
```bash
python3 scripts/benchmark.py --tamanhos 100000,1000000 --reps 3     # só biblioteca padrão
pip install matplotlib reportlab
python3 scripts/gerar_relatorio.py --entrada resultados --pdf docs/Relatorio_Threads_II_RotaVital.pdf
```
O benchmark mede o tempo de resposta HTTP (mediana), confere se todas as versões deram a mesma
resposta, roda a demo de race condition e grava `resultados/resumo.csv`, `medicoes_brutas.csv` e
`meta.json`. O gerador monta tabela, gráficos, speedup e a análise com os números medidos.

> Rode o benchmark numa máquina com vários núcleos e com o mínimo de programas abertos. O speedup
> depende dos núcleos disponíveis (`/api/info` → `nucleosDisponiveis`).

## Testes
```bash
./mvnw test     # verifica que plataforma (2,3,4,8,16) e virtual (2,4,8) == sequencial
```

## Estrutura
```
src/main/java/br/com/rotavital/
├── RotaVitalApplication.java
├── dominio/      Uf, BaseDeDados (colunas em arrays), GeradorDeDados (semente fixa)
├── cruzamento/   MotorCruzamento (sequencial, plataforma, virtual, inseguro), Parcial, ResultadoCruzamento
├── servico/      CruzamentoService (pools reaproveitados), RepositorioDeDados (snapshots em memória)
└── web/          CruzamentoController (REST)
scripts/          benchmark.py, gerar_relatorio.py
docs/             relatório em PDF e gráficos
```
