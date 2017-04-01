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

// Welsh (cy)
    plupload.addI18n({
        "Stop Upload": "Atal Lanlwytho",
        "Upload URL might be wrong or doesn't exist.": "URL y lanlwythiad ynb anghywir neu ddim yn bodoli.",
        "tb": "tb",
        "Size": "Maint",
        "Close": "Cau",
        "Init error.": "Gwall cych.",
        "Add files to the upload queue and click the start button.": "Ychwanegwch ffeiliau i'r ciw lanlwytho a chlicio'r botwm dechrau.",
        "Filename": "Enw'r ffeil",
        "Image format either wrong or not supported.": "Fformat delwedd yn anghywir neu heb ei gynnal.",
        "Status": "Statws",
        "HTTP Error.": "Gwall HTTP.",
        "Start Upload": "Dechrau Lanlwytho",
        "mb": "mb",
        "kb": "kb",
        "Duplicate file error.": "Gwall ffeil ddyblyg.",
        "File size error.": "Gwall maint ffeil.",
        "N/A": "Dd/A",
        "gb": "gb",
        "Error: Invalid file extension:": "Gwall: estyniad ffeil annilys:",
        "Select files": "Dewis ffeiliau",
        "%s already present in the queue.": "%s yn y ciw yn barod.",
        "File: %s": "Ffeil: %s",
        "b": "b",
        "Uploaded %d/%d files": "Lanlwythwyd  %d/%d ffeil",
        "Upload element accepts only %d file(s) at a time. Extra files were stripped.": "Mae'r elfen lanlwytho yn derbyn %d ffeil ar y tro. Caiff ffeiliau ychwanegol eu tynnu.",
        "%d files queued": "%d ffeil mewn ciw",
        "File: %s, size: %d, max file size: %d": "Ffeil: %s, maint: %d, maint mwyaf ffeil: %d",
        "Drag files here.": "Llusgwch ffeiliau yma.",
        "Runtime ran out of available memory.": "Allan o gof.",
        "File count error.": "Gwall cyfri ffeiliau.",
        "File extension error.": "Gwall estyniad ffeil.",
        "Error: File too large:": "Gwall: Ffeil yn rhy fawr:",
        "Add Files": "Ychwanegu Ffeiliau"
    });

}));