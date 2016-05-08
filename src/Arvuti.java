import java.time.LocalDateTime;

/**
 * Created by HansDaniel on 5.05.2016.
 */
public class Arvuti implements Kompuuter {

    private String tootja;
    private boolean kiirtöö;
    private java.time.LocalDateTime aeg;
    private Double arveSumma;


    public Arvuti(String tootja, boolean kiirtöö, LocalDateTime aeg) {
        this.tootja = tootja;
        this.kiirtöö = kiirtöö;
        this.aeg = aeg;
        this.arveSumma = 0.0;
    }


    @Override
    public String getTootja() {
        return tootja;
    }

    @Override
    public boolean onKiirtöö() {
        return kiirtöö;
    }

    @Override
    public LocalDateTime getRegistreerimiseAeg() {
        return aeg;
    }

    @Override
    public Double getArveSumma() {
        return arveSumma;
    }

    @Override
    public void lõpetaTöö(int minutid, double baasHind, Arvutiparandus arvutiparandus) {
        Double hind = 2.0 + minutid * baasHind/60;
        if (onKiirtöö()){
            hind+=10;
        }
        arveSumma = Math.floor(hind*100) / 100;
        arvutiparandus.lisaLõpetatud(this.getTootja());
        arvutiparandus.lisaSummale(arveSumma);
        System.out.println("Töö tehtud, arve summa on:"+arveSumma);
    }

    @Override
    public int compareTo(Kompuuter o) {
        if (kiirtöö!=o.onKiirtöö()){
            if(kiirtöö) return 1;
            return -1;
        }
        return o.getRegistreerimiseAeg().compareTo(aeg);
    }

    @Override
    public String toString(){
        String tööTüüp;
        if (onKiirtöö()){
            tööTüüp = "kiirtöö";
        }
        else{
            tööTüüp = "tavatöö";
        }
        return (this.getTootja()+";"+tööTüüp+"|"+this.getRegistreerimiseAeg());
    }
}
