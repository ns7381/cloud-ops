!function(a){"function"==typeof define&&define.amd?define(["jquery","../jquery.validate"],a):a(jQuery)}(function(a){a.extend(a.validator.messages,{required:"Aquest camp és obligatori.",remote:"Si us plau, omple aquest camp.",email:"Si us plau, escriu una adreça de correu-e vàlida",url:"Si us plau, escriu una URL vàlida.",date:"Si us plau, escriu una data vàlida.",dateISO:"Si us plau, escriu una data (ISO) vàlida.",number:"Si us plau, escriu un número enter vàlid.",digits:"Si us plau, escriu només dígits.",creditcard:"Si us plau, escriu un número de tarjeta vàlid.",equalTo:"Si us plau, escriu el maateix valor de nou.",extension:"Si us plau, escriu un valor amb una extensió acceptada.",maxlength:a.validator.format("Si us plau, no escriguis més de {0} caracters."),minlength:a.validator.format("Si us plau, no escriguis menys de {0} caracters."),rangelength:a.validator.format("Si us plau, escriu un valor entre {0} i {1} caracters."),range:a.validator.format("Si us plau, escriu un valor entre {0} i {1}."),max:a.validator.format("Si us plau, escriu un valor menor o igual a {0}."),min:a.validator.format("Si us plau, escriu un valor major o igual a {0}.")})});