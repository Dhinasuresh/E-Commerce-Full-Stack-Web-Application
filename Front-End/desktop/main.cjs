const { app, BrowserWindow, dialog } = require("electron");
const { spawn } = require("child_process");
const http = require("http");
const path = require("path");

const API_PORT = 8080;
const DEV_URL = "http://localhost:5173";
let backendProcess = null;

function isDevelopment() {
  return !app.isPackaged;
}

function waitForBackend(timeoutMs = 30000) {
  const startedAt = Date.now();

  return new Promise((resolve, reject) => {
    const tryConnect = () => {
      const request = http.get(`http://127.0.0.1:${API_PORT}/api/dashboard/summary`, (response) => {
        response.resume();
        resolve();
      });

      request.on("error", () => {
        if (Date.now() - startedAt >= timeoutMs) {
          reject(new Error("Backend startup timed out."));
          return;
        }
        setTimeout(tryConnect, 1000);
      });
    };

    tryConnect();
  });
}

function startBackend() {
  if (backendProcess) {
    return;
  }

  if (isDevelopment()) {
    const projectRoot = path.resolve(__dirname, "..", "..");
    backendProcess = spawn("cmd.exe", ["/c", "run-backend.cmd"], {
      cwd: projectRoot,
      windowsHide: true,
    });
    return;
  }

  const backendJarCandidates = [
    path.join(process.resourcesPath, "backend", "todo-app.jar"),
    path.join(process.resourcesPath, "todo-app-0.0.1-SNAPSHOT.jar"),
  ];
  const backendJar = backendJarCandidates.find((candidate) => require("fs").existsSync(candidate));
  if (!backendJar) {
    dialog.showErrorBox("Backend file missing", "The desktop app could not find the packaged backend jar.");
    return;
  }
  backendProcess = spawn("java", ["-jar", backendJar], {
    windowsHide: true,
  });
}

function stopBackend() {
  if (backendProcess && !backendProcess.killed) {
    backendProcess.kill();
  }
  backendProcess = null;
}

async function createWindow() {
  startBackend();

  try {
    await waitForBackend();
  } catch (error) {
    dialog.showErrorBox("Backend startup failed", error.message);
  }

  const window = new BrowserWindow({
    width: 1440,
    height: 920,
    minWidth: 1100,
    minHeight: 760,
    backgroundColor: "#f4f7e8",
    autoHideMenuBar: true,
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  if (isDevelopment()) {
    await window.loadURL(DEV_URL);
    return;
  }

  await window.loadFile(path.join(__dirname, "..", "dist", "index.html"));
}

app.whenReady().then(createWindow);

app.on("window-all-closed", () => {
  stopBackend();
  if (process.platform !== "darwin") {
    app.quit();
  }
});

app.on("before-quit", () => {
  stopBackend();
});
