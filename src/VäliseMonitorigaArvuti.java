import java.time.LocalDateTime;


public class VäliseMonitorigaArvuti extends Arvuti{
    private String tootja;
    private boolean kiirtöö;
    private java.time.LocalDateTime aeg;
    private Double arveSumma;

    public VäliseMonitorigaArvuti(String tootja, boolean kiirtöö, LocalDateTime aeg) {
        super(tootja, kiirtöö, aeg);
    }


    @Override
    public void lõpetaTöö(int minutid, double baasHind, Arvutiparandus arvutiparandus) {
        Double hind = 3.0 + minutid * baasHind/60;
        if (onKiirtöö()){
            hind+=10;
        }
        arveSumma = Math.floor(hind*100) / 100;
        arvutiparandus.lisaLõpetatud(this.getTootja());
        arvutiparandus.lisaSummale(arveSumma);
        System.out.println("Töö tehtud, arve summa on:"+arveSumma);
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
        return (this.getTootja()+";"+tööTüüp+";monitoriga"+"|"+this.getRegistreerimiseAeg());
    }
}
