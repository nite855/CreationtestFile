$ErrorActionPreference = "Stop"

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    throw "Maven is required to build this Spring Boot project. Install Maven or add a Maven wrapper."
}

mvn test
