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

// Azerbaijani (az)
    plupload.addI18n({
        "Stop Upload": "Yükləməni saxla",
        "Upload URL might be wrong or doesn't exist.": "Yükləmə ünvanı səhvdir və ya mövcud deyil",
        "tb": "tb",
        "Size": "Həcm",
        "Close": "Bağla",
        "Init error.": "Init error.",
        "Add files to the upload queue and click the start button.": "Faylları əlavə edin və yüklə düyməsinə klikləyin.",
        "Filename": "Faylın adı",
        "Image format either wrong or not supported.": "Şəklin formatı uyğun deyil və ya dəstəklənmir.",
        "Status": "Status",
        "HTTP Error.": "HTTP xətası.",
        "Start Upload": "Yüklə",
        "mb": "mb",
        "kb": "kb",
        "Duplicate file error.": "Bu fayl artıq növbədə var.",
        "File size error.": "Fayl həcmi xətası.",
        "N/A": "N/A",
        "gb": "gb",
        "Error: Invalid file extension:": "Xəta: Yanlış fayl uzantısı:",
        "Select files": "Faylları seçin",
        "%s already present in the queue.": "%s artıq növbədə var.",
        "File: %s": "Fayl: %s",
        "b": "b",
        "Uploaded %d/%d files": "%d/%d fayl yüklənib",
        "Upload element accepts only %d file(s) at a time. Extra files were stripped.": "Upload element accepts only %d file(s) at a time. Extra files were stripped.",
        "%d files queued": "Növbədə %d fayl var",
        "File: %s, size: %d, max file size: %d": "Fayl: %s, həcm: %d, max fayl həcmi: %d",
        "Drag files here.": "Faylları bura çəkin.",
        "Runtime ran out of available memory.": "Runtime ran out of available memory.",
        "File count error.": "Fayl sayı çox böyükdür.",
        "File extension error.": "Fayl uzantısı xətası.",
        "Error: File too large:": "Xəta:Fayl həcmi çox böyükdür.",
        "Add Files": "Fayl əlavə et"
    });

}));