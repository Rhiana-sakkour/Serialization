# Serialization Lab - JSON / Java Serialization / Protocol Buffers

Complete Maven project covering the three experiments and the report questions.

## 1) Requirements
| Tool | Version | Check |
|---|---|---|
| JDK | 11 or newer (17/21 recommended) | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| curl | any | `curl.exe --version` (Windows 10/11 ships it) |
| Internet | first build only | Maven downloads the libraries and `protoc` automatically |

You do NOT need to install `protoc`: `protobuf-maven-plugin` downloads the right binary for your OS.

## 2) Quick start
```powershell
# Windows PowerShell (note the leading .\ )
.\run-all.bat
```
```bash
# Linux / macOS / WSL / Git-Bash
./run-all.sh
```
All outputs are saved in `results/` (UTF-8 files; open them with VS Code). The first run is slow because of downloads;
`run-all` is silent while it works, so run `mvn compile` once first if you want to watch the progress.

## 3) Run one experiment at a time
`.\run.bat <MainClass> [args]` (Windows) or `./run.sh <MainClass> [args]`

| Task | Command |
|---|---|
| 1.2 schema mismatch simulation | `.\run.bat SchemaMismatchDemo` |
| 2.2 Java serialization / deserialization | `.\run.bat JavaSerializationLab` |
| 2.3 the three exploration experiments | `.\run.bat Task23Experiments` |
| 2.3 static field across two JVMs | `.\run.bat StaticFieldCrossJvmDemo write` then `.\run.bat StaticFieldCrossJvmDemo read` |
| 3.2 Protocol Buffers | `.\run.bat ProtobufLab` |
| Report Q2: sizes + timing | `.\run.bat SizeComparison` |
| Report Q2: renamed field / changed tag | `.\run.bat ProtobufRenameExperiment` |

## 4) Task 1.1 - JSON server + curl
Window 1 (server):
```powershell
.\run.bat AnalyzeServer        # http://localhost:8080  (Ctrl+C to stop)
```
Window 2 (from the project root; on PowerShell use `curl.exe`, not `curl`):
```powershell
curl.exe -X POST http://localhost:8080/api/analyze -H "Content-Type: application/json" -d "@customer_request.json"
curl.exe -i -X POST http://localhost:8080/api/prices/product -H "Content-Type: application/json" -d "@payload_prices_sender.json"
curl.exe -i -X POST http://localhost:8080/api/prices/product -H "Content-Type: application/json" -d "@payload_prices_receiver.json"
```
Java alternative (same `HttpClient` + `CompletableFuture` style as Lab 2): `.\run.bat JsonHttpClientDemo`.

## 5) Files
```
pom.xml                      Maven (protobuf-java 3.25.5, jackson 2.17.2, protoc downloaded automatically)
customer_request.json        task 1.1
payload_prices_*.json        task 1.2 (curl)
src/main/proto/              customer_profile.proto (task 3.1) + receiver-side copies (renamed field / changed tag)
src/main/java/               all programs
run.sh / run.bat / run-all.sh / run-all.bat
```
Generated stub (task 3.2, step 1): `target/generated-sources/protobuf/java/com/distributed/lab/CustomerProfileProto.java`

To run `protoc` by hand as in the lab text, you can reuse the binary that Maven downloaded (PowerShell; the output goes to
`generated-manual/`, which is git-ignored, so it does not clash with the Maven-generated stub):
```powershell
mkdir generated-manual
$protoc = (Get-ChildItem target\protoc-plugins\protoc-*.exe | Select-Object -First 1).FullName
& $protoc --version
& $protoc -I src\main\proto --java_out=generated-manual src\main\proto\customer_profile.proto
```

## 6) Results and report
- `results/` holds the output of `run-all` (one text file per experiment). `customer.ser` in that folder is a binary file written by
  `StaticFieldCrossJvmDemo` and is not committed (see `.gitignore`).
- `report/Serialization_Lab_Report.docx` (and a PDF copy) is the lab report with the outputs, the answers to the report questions and the four closing questions.

## 7) Troubleshooting
- `release version 11 not supported` -> JDK older than 11; install JDK 17+.
- `protoc` download fails -> check internet/proxy settings in `~/.m2/settings.xml`.
- Port 8080 busy -> `.\run.bat AnalyzeServer 8081` and change the port in the curl URLs.
