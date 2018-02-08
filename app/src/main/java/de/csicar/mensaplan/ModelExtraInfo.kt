package de.csicar.mensaplan

import android.net.Uri

/**
 * Created by csicar on 08.02.18.
 */
data class CanteenInfo(val maps : Uri?, val niceName : String, val image : String?, val openTimes : String?)

object Canteens {
    val default = mapOf<String, CanteenInfo>(
            "adenauerring" to CanteenInfo(
                    Uri.parse("https://www.google.de/maps/place/Studierendenwerk+Karlsruhe+-+Mensa+am+Adenauerring/@49.0536779,8.447408,12.25z/data=!4m8!1m2!2m1!1skit+mensa!3m4!1s0x47970636b081742f:0x2d93d5ea5b39cdbe!8m2!3d49.0117188!4d8.4169517?hl=de"),
                            "Mensa am Adenauerring",
                            "https://www.regio-news.de/images/2017/10/mensa_kit.jpg",
                            "Mittagessen\n" +
                                    "Mo. - Fr. 11:00 - 14:00 Uhr\n" +
                                    "\n" +
                                    "[Kœri]werk\n" +
                                    "Mo. - Do. 11:00 - 14:30 Uhr\n" +
                                    "Fr. 11:00 - 14:00 Uhr\n" +
                                    "\n" +
                                    "Abendessen in der Cafeteria am Adenauerring\n" +
                                    "Mo. - Do. 16:00 - 19:30 Uhr"
                    ),
            "tiefenbronner" to CanteenInfo(Uri.parse("https://www.google.de/maps/place/Hochschule+Pforzheim+-+Mensa/@48.8783439,8.7163299,19z/data=!3m1!4b1!4m5!3m4!1s0x479771e35def9a99:0xfb34f6c4844782d4!8m2!3d48.8783439!4d8.7168771"),
                    "Mensa Hochschule Pforzheim",
                    "http://www.sw-ka.de/media_lib_files/2619_essen_mensa_tiefenbronner.jpg",
                    "Öffnungszeiten\n" +
                            "Mo. - Fr. 11:00 - 14:00 Uhr"),
            "moltke" to CanteenInfo(Uri.parse("http://geodaten.karlsruhe.de/stadtplan/index.jsp?conf=websis-config&x=3455753.6666666665&y=5430997.3&scale=4787&m_meetingpoint_pin=true&m_meetingpoint_pin_img=star.png&m_meetingpoint_pin_x=3455753.6666666665&m_meetingpoint_pin_y=5430997.3"),
                    "Mensa Moltke",
                    "http://www.sw-ka.de/media_lib_files/398_mensa_moltke_nacht.jpg",
                    "Mittagessen\n" +
                            "Mo. - Fr. 11:15 - 14:15 Uhr"),
            "erzberger" to CanteenInfo(Uri.parse("http://geodaten.karlsruhe.de/stadtplan/index.jsp?conf=websis-config&x=3455140.3333333335&y=5432359.166666667&scale=4787&m_meetingpoint_pin=true&m_meetingpoint_pin_img=star.png&m_meetingpoint_pin_x=3455140.3333333335&m_meetingpoint_pin_y=5432359.166666667"),
                    "Mensa Erzbergerstraße",
                    null,
                    "\n" +
                            "Öffnungszeiten\n" +
                            "Mittagessen\n" +
                            "Mo. - Fr. 11:15 - 13:30 Uhr\n" +
                            "\n" +
                            "Caféteria\n" +
                            "Mo. - Do. 7:45 - 15:30 Uhr\n" +
                            "Fr. 7:45 - 14:00 Uhr"),
            "gottesaue" to CanteenInfo(Uri.parse("http://geodaten.karlsruhe.de/stadtplan/index.jsp?conf=websis-config&x=3458198.3333333335&y=5429862.033333333&scale=4787&m_meetingpoint_pin=true&m_meetingpoint_pin_img=star.png&m_meetingpoint_pin_x=3458198.3333333335&m_meetingpoint_pin_y=5429862.033333333"),
                    "Mensa Schloss Gottesaue",
                    "http://www.sw-ka.de/media_lib_files/2618_essen_mensa_cafeteria_gottesaue.jpg",
                    "Öffnungszeiten\n" +
                            "Mo. - Do.: 09:00 - 15:00 Uhr\n" +
                            "Mittagessen\n" +
                            "Mo. - Fr. 12:00 - 14:00 Uhr\n" +
                            "\n" +
                            "Caféteria\n" +
                            "Mo. - Fr. 09:00 - 15:00 Uhr"),
            "holzgarten" to CanteenInfo(Uri.parse("https://www.google.de/maps/place/hochschule+mensa/@48.887442,8.7081963,18.5z/data=!4m8!1m2!2m1!1smensa+in+der+N%C3%A4he+von+Holzgartenstra%C3%9Fe,+Pforzheim!3m4!1s0x0:0xaae4826de5522ac7!8m2!3d48.887924!4d8.7082341"),
            "Mensa Holzgartenstraße",
                    null,
                    "Öffnungszeiten\n" +
                            "Mo. - Fr. 11:15 - 13:30 Uhr\n" +
                            "\n" +
                            "Caféteria\n" +
                            "Mo. - Fr. 10:15 - 14:00 Uhr")
    )
}