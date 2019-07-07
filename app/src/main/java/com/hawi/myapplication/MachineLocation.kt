package com.hawi.myapplication

//{"sm_id":"12","sm_name":"Some place","sm_dist":"effat","sm_lat":"21.9512","sm_long":"39.0432"}

class MachineLocation {
    var ml_id:Int? = null
    var ml_name:String? = null
    var ml_dist:String? = null
    var ml_lat:Double? = null
    var ml_long:Double? = null

    constructor(ml_id: Int?, ml_name: String?, ml_dist: String?, ml_lat: Double?, ml_long: Double?) {
        this.ml_id = ml_id
        this.ml_name = ml_name
        this.ml_dist = ml_dist
        this.ml_lat = ml_lat
        this.ml_long = ml_long
    }
}