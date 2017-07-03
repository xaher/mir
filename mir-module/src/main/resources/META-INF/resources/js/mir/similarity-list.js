/**
 * Created by sven on 26.06.17.
 */

$(document).ready(function () {

    var comparativID;

    function getList() {
        $.ajax({
            url: webApplicationBaseURL + "rsc/moreLikeThis",
            type: "GET",
            dataType: "json",
            success: function (data) {
                $.each(data, function (key, comparativObject) {
                    var l = $("<ul id='list-group' class='input-group'></ul>");
                    $.each(comparativObject, function (index, similarityObject) {
                        $(l).append("<div id='" + "item-" + index + "' class='input-group hide-document'>" + "<a href='" + webApplicationBaseURL
                            + "receive/" + similarityObject.objectID + "' class='list-group-item' target='_blank'>"
                            + " ID " + similarityObject.objectID + " Score: " + similarityObject.score + "</a>"
                            + "<span id='doublet' class='input-group-btn'>" + "<button id='" + "danger-" + index + "'class='btn btn-danger doublet-button' data-similarity-id='" + similarityObject.id + "'>"
                            + "Dublette" + "</button>" + "<button id='" + "success-" + index + "' class='btn btn-success hide-button'>"
                            + "keine Dublette" + "</button>" + "</span>" + "</div>");

                    });
                    var li = $("<li class='list'>" +
                        "<a href='" + webApplicationBaseURL + "receive/" + key
                        + "' class='btn btn-default comparativ-document'" +
                        "target='_blank' data-comparativ-id='" + key + "'>" + key + "</a>" + "</li>");
                    $(li).append(l);
                    $('.new-list').append(li);
                    comparativID = key;
                });
                $('.new-list > li > ul');

            },
            error: function (error) {
                console.log(error);
            }

        });
    }


    $('#list').on('click', getList);
    $('#element').on('click', getList);

    $("similarityList").on("click", ".doublet-button", function () {
        var itemDiv = $(this).closest("div.hide-document");
        var id = $(this).attr("data-similarity-id");
        ajaxDoubletOf(id, comparativID).then(function () {
            removeItem(itemDiv);
        }).fail(function (error) {
            console.log(error);
        });
    });

    $("similarityList").on("click", ".hide-button", function () {
        var itemDiv = $(this).closest("div.hide-document");
        removeItem(itemDiv);
    });

    function removeItem(item) {
    item.remove();
    if ($(".new-list .hide-document").length == 0) {
        ajaxDoubletOf(comparativID, "checked").then(function () {
            getList();
        }).fail(function (error) {
            console.log(error);
        });
    }
    }

    function ajaxDoubletOf(id, doubletOf) {
    return $.ajax({
        url: webApplicationBaseURL + "rsc/moreLikeThis?id=" + id + "&doubletOf=" + doubletOf,
        type: "POST"
    });
    }

});