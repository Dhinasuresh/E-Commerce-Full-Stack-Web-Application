@echo off
setlocal

for /d %%D in ("%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.14-bin\*") do (
  if exist "%%D\apache-maven-3.9.14\bin\mvn.cmd" (
    set "MVN=%%D\apache-maven-3.9.14\bin\mvn.cmd"
  )
)

if not defined MVN (
  echo Maven 3.9.14 was not found in the local wrapper cache.
  exit /b 1
)

set "REPO=%USERPROFILE%\.m2\repository"
cd /d "%~dp0BackEnd\todo-app"
call "%MVN%" -Dmaven.repo.local=%REPO% -DskipTests package
