package com.jans.tiles.app.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class DashboardModel(
    @SerializedName("dashboard")
    @Expose
    var dashboard: List<Dashboard> = listOf()
) {
    data class Dashboard(
        @SerializedName("block")
        @Expose
        var block: String = "",
        @SerializedName("cellBackgroundColor")
        @Expose
        var cellBackgroundColor: String = "",
        @SerializedName("cellLayoutSameasModule")
        @Expose
        var cellLayoutSameasModule: Boolean = false,
        @SerializedName("id")
        @Expose
        var id: Int = 0,
        @SerializedName("imagename")
        @Expose
        var imagename: String = "",
        @SerializedName("microsite")
        @Expose
        var microsite: Boolean = false,
        @SerializedName("navTitle")
        @Expose
        var navTitle: String? = null,
        @SerializedName("pushInternalKey")
        @Expose
        var pushInternalKey: String = "",
        @SerializedName("submodules")
        @Expose
        var submodules: List<Submodule> = listOf(),
        @SerializedName("tintcolor")
        @Expose
        var tintcolor: String = "",
        @SerializedName("title")
        @Expose
        var title: String = "",
        @SerializedName("url")
        @Expose
        var url: String = ""
    ) {
        data class Submodule(
            @SerializedName("block")
            @Expose
            var block: String = "",
            @SerializedName("id")
            @Expose
            var id: Int = 0,
            @SerializedName("imagename")
            @Expose
            var imagename: String = "",
            @SerializedName("microsite")
            @Expose
            var microsite: Boolean? = null,
            @SerializedName("navTitle")
            @Expose
            var navTitle: String? = null,
            @SerializedName("pushInternalKey")
            @Expose
            var pushInternalKey: String = "",
            @SerializedName("title")
            @Expose
            var title: String = "",
            @SerializedName("url")
            @Expose
            var url: String = ""
        )
    }
}