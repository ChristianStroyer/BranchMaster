"use strict";

var branchmaster = {
    server : null
}
Object.preventExtensions(branchmaster);

branchmaster.server = {

    SERVER: "http://localhost:8101",

    // **********************
    //		REST API
    // **********************
    dir: function (branches, success_callback, fail_callback) {
        $.ajax({
            type: "POST",
            url: this.SERVER + "/dir",
            data: {
                branches: branches
            }
        }).done(function (msg) {
            if (success_callback != null)
                success_callback(msg);
        }).fail(function (jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
            if (fail_callback != null)
                fail_callback(textStatus);
        });
    },

    gittree: function (id, lattitude, longitude, accuracy, success_callback, fail_callback) {
        $.ajax({
            type: "POST",
            url: this.SERVER + "/api/onmyway/update",
            data: {
                id: id,
                longitude: longitude,
                lattitude: lattitude,
                accuracy: accuracy
            }
        }).done(function (msg) {
            if (success_callback != null)
                success_callback(msg);
        }).fail(function (jqXHR, textStatus) {
            console.log("Request failed: " + textStatus);
            if (fail_callback != null)
                fail_callback(textStatus);
        });
    },

};

Object.preventExtensions(branchmaster.server);