function savePrefs() {
    if(prefs != null) {
        localStorage.setItem("prefs", JSON.stringify(prefs))
    }
}
function loadPrefs() {
    let saved = localStorage.getItem("prefs")
    if(saved) {
        prefs = JSON.parse(saved);
    }
}
loadPrefs();