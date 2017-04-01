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

// Swedish (sv)
    plupload.addI18n({
        "Stop Upload": "Avbryt",
        "Upload URL might be wrong or doesn't exist.": "URL:en va fel eller existerar inte.",
        "tb": "tb",
        "Size": "Storlek",
        "Close": "Stäng",
        "Init error.": "Problem vid initialisering.",
        "Add files to the upload queue and click the start button.": "Lägg till filer till kön och tryck på start.",
        "Filename": "Filnamn",
        "Image format either wrong or not supported.": "Bildformatet är fel eller så finns inte stöd för det.",
        "Status": "Status",
        "HTTP Error.": "HTTP problem.",
        "Start Upload": "Starta",
        "mb": "mb",
        "kb": "kb",
        "Duplicate file error.": "Problem med dubbla filer.",
        "File size error.": "Problem med filstorlek.",
        "N/A": "N/A",
        "gb": "gb",
        "Error: Invalid file extension:": "Fel: Ej godkänd filändelse.",
        "Select files": "Välj filer",
        "%s already present in the queue.": "%s är redan tillagd.",
        "File: %s": "Fil: %s",
        "b": "b",
        "Uploaded %d/%d files": "Laddade upp %d/%d filer",
        "Upload element accepts only %d file(s) at a time. Extra files were stripped.": "Det går bara lägga till %d filer åt gången, allt utöver detta togs bort.",
        "%d files queued": "%d filer i kö",
        "File: %s, size: %d, max file size: %d": "Fil: %s, storlek: %d, max storlek: %d",
        "Drag files here.": "Dra filer hit",
        "Runtime ran out of available memory.": "Slut på minne.",
        "File count error.": "Räknefel.",
        "File extension error.": "Problem med filändelse.",
        "Error: File too large:": "Fel: Filen är för stor:",
        "Add Files": "Lägg till"
    });

}));