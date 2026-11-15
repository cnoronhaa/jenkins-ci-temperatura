# Build e testes no Jenkins com GitHub

O job `temperatura-ci` lê o `Jenkinsfile` e os fontes Java do GitHub. O Maven compila o conversor, o JUnit executa quatro testes e o Jenkins registra o resultado. Quando o parâmetro `COBERTURA` está marcado, o JaCoCo mede as linhas executadas pelos testes.

| Método | Fórmula | Casos cobertos |
| --- | --- | --- |
| `fahrenheitParaCelsius` | `(F − 32) × 5 ÷ 9` | 32 °F → 0 °C e 98,6 °F → 37 °C |
| `celsiusParaFahrenheit` | `C × 9 ÷ 5 + 32` | −40 °C → −40 °F e 100 °C → 212 °F |

Os testes comparam valores `double` com uma tolerância de `1e-9`.

## Preparação no PowerShell

Use Git e um JDK 21 ou posterior para o projeto. O Maven Wrapper baixa a versão do Maven especificada no repositório na primeira execução.

```powershell
git clone https://github.com/cnoronhaa/jenkins-ci-temperatura.git
Set-Location jenkins-ci-temperatura
git fetch origin
git branch -r
java -version
.\mvnw.cmd -B clean test
```

A última linha executa os testes fora do Jenkins. Se você já tem um clone, abra o PowerShell na pasta do projeto e comece por `git fetch origin`.

## Configuração do job

1. Abra `http://127.0.0.1:8080/`. Em **Manage Jenkins → Plugins**, confira **Pipeline**, **Git**, **JUnit**, **Coverage** e **Pipeline: Stage View**.
2. Em **New Item**, crie `temperatura-ci` como **Pipeline**. Se o job já existir, abra **Configure**.
3. Em **Pipeline**, selecione **Pipeline script from SCM** e **Git**. Use `https://github.com/cnoronhaa/jenkins-ci-temperatura.git` como **Repository URL**, **Credentials** em `- none -`, `*/main` em **Branch Specifier** e `Jenkinsfile` em **Script Path**. Deixe **Lightweight checkout** marcado.
4. Deixe **Build periodically** desmarcado e salve. Na primeira execução de um job recém-criado, o botão pode ser **Build Now**. Ao ler o `Jenkinsfile`, o Jenkins registra `COBERTURA` e passa a oferecer **Build with Parameters**.

Abra o job em `http://127.0.0.1:8080/job/temperatura-ci/`. Em cada gravação, clique no número da execução e use **Console Output** para mostrar a origem, o SHA e as etapas. A tela **Test Result** mostra o total de testes e detalhes das falhas. Grave a janela do Jenkins e a do PowerShell. Entre no Jenkins antes de iniciar a captura.

## 1. Sucesso manual

1. No PowerShell, mostre o commit da branch correta.

   ```powershell
   git log -1 --oneline origin/main
   ```

2. Em **Configure**, confira `*/main` em **Branch Specifier**. No job, clique em **Build with Parameters**, deixe `COBERTURA` desmarcado e clique em **Build**.
3. Em **Console Output**, aponte o SHA do checkout e `Finished: SUCCESS`. Em **Test Result**, mostre os quatro testes aprovados.

## 2. Falha de compilação

1. Mostre a linha do `return` que perdeu o ponto e vírgula.

   ```powershell
   git diff origin/main origin/demo/falha-compilacao -- src/main/java
   ```

2. Em **Configure → Pipeline → Branch Specifier**, use `*/demo/falha-compilacao` e salve. Dispare manualmente com `COBERTURA` desmarcado.
3. No console, mostre `COMPILATION ERROR`, o arquivo e a linha, a etapa **Testes** pulada e `Finished: FAILURE`.

## 3. Compilação aprovada com testes falhando

1. Mostre a troca de `32.0` por `30.0` na fórmula de Fahrenheit para Celsius.

   ```powershell
   git diff origin/main origin/demo/falha-teste -- src/main/java
   ```

2. Em **Configure → Pipeline → Branch Specifier**, use `*/demo/falha-teste` e salve. Dispare manualmente com `COBERTURA` desmarcado.
3. No console, mostre `BUILD SUCCESS` na etapa **Compilação** e `Finished: UNSTABLE` no fim da execução. Em **Test Result**, abra uma das duas falhas e mostre os valores esperado e obtido. Os dois testes de Celsius para Fahrenheit passam.

O Surefire grava os resultados em XML. O passo `junit` lê esse relatório e marca o build como instável quando encontra falhas. Se o Maven falhar antes de gerar o relatório, ou se o relatório obrigatório estiver ausente, o Jenkins marca o build como falho.

## 4. Execução agendada

1. Em **Configure**, volte a **Branch Specifier** para `*/main`. Na seção **Triggers**, marque **Build periodically** e cole a expressão abaixo em **Schedule**. Salve.

   ```text
   */2 * * * *
   ```

2. Aguarde a próxima execução na página do job sem clicar em **Build with Parameters**. A expressão agenda uma execução a cada dois minutos.
3. Abra a execução que apareceu. Mostre `Started by timer`, os quatro testes aprovados e `Finished: SUCCESS`. Depois da gravação, desmarque **Build periodically** e salve.

## 5. Bônus com cobertura

1. Em `*/main`, clique em **Build with Parameters**, marque `COBERTURA` e clique em **Build**.
2. Abra a execução, mostre `SUCCESS` e os quatro testes aprovados. Em **Coverage**, mostre o percentual da métrica **Line Coverage** e abra os detalhes da classe `ConversorTemperatura`.
3. Para demonstrar a geração do mesmo relatório no PowerShell, execute os comandos abaixo. A medição do Jenkins usa o XML que acompanha o HTML do JaCoCo.

   ```powershell
   .\mvnw.cmd -B -Pcobertura clean verify
   Invoke-Item .\target\site\jacoco\index.html
   ```
