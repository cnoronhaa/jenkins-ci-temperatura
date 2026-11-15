# Build e testes automatizados com Jenkins e GitHub

Este projeto demonstra um pipeline de integração contínua para um conversor de temperaturas em Java. O job `temperatura-ci`, executado em um Jenkins local, carrega o [`Jenkinsfile`](Jenkinsfile) e os fontes do GitHub, compila o projeto com Maven e publica os resultados dos testes JUnit. O JaCoCo mede a cobertura de linhas no cenário adicional.

## Conversor e testes

O [conversor](src/main/java/io/github/cnoronhaa/temperatura/ConversorTemperatura.java) contém dois métodos. A [suíte de testes](src/test/java/io/github/cnoronhaa/temperatura/ConversorTemperaturaTest.java) verifica dois casos por método, com tolerância de `1e-9` na comparação dos resultados.

| Método | Fórmula | Casos testados |
| --- | --- | --- |
| `fahrenheitParaCelsius` | `(F − 32) × 5 ÷ 9` | 32 °F → 0 °C e 98,6 °F → 37 °C |
| `celsiusParaFahrenheit` | `C × 9 ÷ 5 + 32` | −40 °C → −40 °F e 100 °C → 212 °F |

## Pipeline

| Etapa | Responsabilidade |
| --- | --- |
| **Checkout** | Busca o código no GitHub e registra o SHA do commit usado na execução. |
| **Compilação** | Compila os fontes Java com o Maven Wrapper. Um erro nesta etapa interrompe o build antes dos testes. |
| **Testes** | Executa a suíte JUnit e publica os resultados do Maven Surefire. Falhas de asserção marcam a execução como `UNSTABLE`. |
| **Cobertura** | Com o parâmetro `COBERTURA` ativo e os testes aprovados, publica o relatório JaCoCo no Jenkins. |

## Resultados no Jenkins

| Execução | Cenário | Branch | Resultado observado |
| --- | --- | --- | --- |
| #1 | Disparo manual com o código correto | `main` | `SUCCESS`. Compilação concluída e quatro testes aprovados. |
| #2 | Disparo manual com um ponto e vírgula removido | [`demo/falha-compilacao`](https://github.com/cnoronhaa/jenkins-ci-temperatura/tree/demo/falha-compilacao) | `FAILURE`. Erro de sintaxe na compilação. A etapa de testes foi pulada. |
| #3 | Disparo manual com uma fórmula incorreta | [`demo/falha-teste`](https://github.com/cnoronhaa/jenkins-ci-temperatura/tree/demo/falha-teste) | `UNSTABLE`. Compilação concluída, com dois testes aprovados e duas falhas no JUnit. |
| #4 | Disparo pelo agendamento do Jenkins | `main` | `SUCCESS`. A execução iniciou pelo temporizador e os quatro testes passaram. |
| #5 | Disparo manual com cobertura | `main` | `SUCCESS`. Quatro testes aprovados e **100% de cobertura de linhas** no conversor, com 2 de 2 linhas cobertas. |
