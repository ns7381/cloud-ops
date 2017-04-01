(function(root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['plupload'], factory);
    } else if(typeof exports === 'object' && typeof module !== 'undefined') {
        var plupload;
        try {
            plupload = require('plupload');
        } catch (err) {
            plupload = root.plupload;
        }
        if (!plupload) throw new Error('plupload dependency not found');
        module.exports = factory(plupload);
    } else {
        if (!root.plupload) throw new Error('plupload dependency not found');
        factory(root.plupload);
    }
}(this, function(plupload) {

// Romanian (ro)
    plupload.addI18n({
        "Stop Upload": "Oprește încărcarea",
        "Upload URL might be wrong or doesn't exist.": "Upload URL might be wrong or doesn't exist.",
        "tb": "tb",
        "Size": "Mărime",
        "Close": "Închide",
        "Init error.": "Eroare inițializare.",
        "Add files to the upload queue and click the start button.": "Adaugă fișiere în lista apoi apasă butonul \"Începe încărcarea\".",
        "Filename": "Nume fișier",
        "Image format either wrong or not supported.": "Formatul de imagine ori este greșit ori nu este suportat.",
        "Status": "Stare",
        "HTTP Error.": "Eroare HTTP",
        "Start Upload": "Începe încărcarea",
        "mb": "mb",
        "kb": "kb",
        "Duplicate file error.": "Eroare duplicat fișier.",
        "File size error.": "Eroare dimensiune fișier.",
        "N/A": "N/A",
        "gb": "gb",
        "Error: Invalid file extension:": "Eroare: Extensia fișierului este invalidă:",
        "Select files": "Selectează fișierele",
        "%s already present in the queue.": "%s există deja în lista de așteptare.",
        "File: %s": "Fișier: %s",
        "b": "b",
        "Uploaded %d/%d files": "Fișiere încărcate %d/%d",
        "Upload element accepts only %d file(s) at a time. Extra files were stripped.": "Upload element accepts only %d file(s) at a time. Extra files were stripped.",
        "%d files queued": "%d fișiere listate",
        "File: %s, size: %d, max file size: %d": "Fișier: %s, mărime: %d, mărime maximă: %d",
        "Drag files here.": "Trage aici fișierele.",
        "Runtime ran out of available memory.": "Runtime ran out of available memory.",
        "File count error.": "Eroare numărare fișiere.",
        "File extension error.": "Eroare extensie fișier.",
        "Error: File too large:": "Eroare: Fișierul este prea mare:",
        "Add Files": "Adaugă fișiere"
    });

}));