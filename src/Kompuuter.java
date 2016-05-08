public interface Kompuuter extends Comparable<Kompuuter>{
    String getTootja();
    boolean onKiirtöö();
    java.time.LocalDateTime getRegistreerimiseAeg();
    Double getArveSumma();
    void lõpetaTöö(int minutid, double baasHind, Arvutiparandus arvutiparandus);

}
