package com.example.alienegg.tamperedentist;

/**
 * Created by AlienNest on 11.4.2016.
 */

import java.util.Comparator;
public class DentistObj implements Comparable<DentistObj>{

    // All data can be found in the path: features -> []
    private String id; //[] -> id:
    //private String geoLocCoordinates // [] -> geometry -> coordinates[]
    private String nimi; // [] -> properties -> NIMI:
    private String osoite; // [] -> properties -> OSOITE:
    private String postinumero; // [] -> properties -> POSTINUMERO:
    private String postitoimipaikka; // [] -> properties -> POSTITOIMIPAIKKA:
    private String linkURL; // [] -> properties -> URL:
    private String puhelin; // [] -> properties -> PUHELIN:

    // Default constructor
    public DentistObj()
    {
        id = "null";
        // geoLocCoordinates = new String["", ""]
        nimi = "unnamed";
        osoite = "";
        postinumero = "";
        postitoimipaikka = "";
        linkURL = "-";
        puhelin = "-";
    }

    // Parameter constructor (used when loading data from servers)
    public DentistObj(String _id, String _nimi, String _osoite,
                      String _postinumero, String _postitoimipaikka, String _linkURL, String _puhelin)
    {
        id = _id;
        // geoLocCoordinates = new String["", ""]
        nimi = _nimi;
        osoite = _osoite;
        postinumero = _postinumero;
        postitoimipaikka = _postitoimipaikka;
        linkURL = _linkURL;
        puhelin = _puhelin;
    }

    // Basic equals function for DentistObj
    /*
		Compares if two DentistObjs are the same.
	*/
    public boolean equals(DentistObj temp)
    {
        if (this.id.equals(temp.id))
            return true;
        else
            return false;
    }

    public String dentistID(){return id; }

    public String dentistName()
    {
        return nimi;
    }

    public String dentistOsoite()
    {
        return osoite;
    }

    public String dentistPostinumero()
    {
        return postinumero;
    }

    public String dentistPostitoimipaikka()
    {
        return postitoimipaikka;
    }

    public String dentistLinkURL()
    {
        return linkURL;
    }

    public String dentistPuhelin()
    {
        return puhelin;
    }


    // Overriding collection comparations.
    // There are used when sorting items in the list.
    // By default it sorts by city name and then by it's date.
    @Override
    public int compareTo(DentistObj dObj) {
        return Comparators.DentistNAMEandID.compare(this, dObj);
    }


    public static class Comparators
    {
        public static Comparator<DentistObj> DentistNAMEandID = new Comparator<DentistObj>()
        {
            @Override
            public int compare(DentistObj x, DentistObj y)
            {
                int i = x.nimi.compareTo(y.nimi);
                if (i == 0)
                    i = x.id.compareTo(y.id);
                return i;
            }
        };
    }
}
