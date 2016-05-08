import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class Arvutiparandus {
    int parandatudArvutiteArv;
    File vigasedKirjeldused;
    HashMap<String,Integer> parandatudInfo;
    List<Kompuuter> parandatudArvutid;
    Scanner scanner;
    List<Kompuuter> parandamataArvutid;
    Double summa;
    BufferedWriter bw;
    HashMap<String, Double> hinnakiri;

    public Arvutiparandus(File vigasedKirjeldused, Scanner scanner, HashMap<String, Double> hinnakiri) throws IOException {
        this.vigasedKirjeldused = vigasedKirjeldused;
        this.scanner = scanner;
        this.parandatudArvutiteArv = 0;
        this.parandatudInfo = new HashMap<>();
        this.parandatudArvutid = new ArrayList<>();
        this.parandamataArvutid = new ArrayList<>();
        this.hinnakiri = hinnakiri;
        this.summa = 0.0;
        this.bw = new BufferedWriter(new FileWriter(vigasedKirjeldused.getAbsoluteFile()));
    }

    public static void main(String[] args) throws IOException {

        String path = args[0];
        List<String> kirjed;
        if(path.startsWith("http://")||path.startsWith("https://")){
            kirjed = loeURList(path);
        }
        else{
            kirjed = loeFailist(path);
        }

        List<Kompuuter> kompuuterList = new ArrayList<>();

        File file = new File("vigased_kirjeldused.txt");
        Scanner scanner = new Scanner(System.in);

        HashMap<String, Double> hinnakiri = loeTasud("tunnitasud.dat");

        Arvutiparandus arvutiparandus = new Arvutiparandus(file,scanner,hinnakiri);

        for (String kirje:kirjed) {
            try {
                arvutiparandus.parandamataArvutid.add(arvutiparandus.loeArvuti(kirje));
            } catch (Formaadierind formaadierind) {
                System.out.println("Vigane sisestus! Viga "+formaadierind.getViganeIndex()+". väljas.");
            }
        }

        boolean end = true;

        while(end){
            System.out.println("Kas soovid parandada (P), uut tööd registreerida (R) või lõpetada (L) ?");
            String otsus =scanner.nextLine();
            if(otsus.equals("P")){
                arvutiparandus.parandaArvuti();
            }
            else if(otsus.equals("R")){
                arvutiparandus.ootelArvutiLisamine();
            }
            else if(otsus.equals("L")){
                end = arvutiparandus.tööLõpetamine();
            }
        }





    }




    public static List<String> loeURList(String path) throws IOException {
        List<String> list = new ArrayList<>();
        URL file = new URL(path);
        try(
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(file.openStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                list.add(inputLine);

            return list;
        }
    }

    public static List<String> loeFailist(String path) throws FileNotFoundException {
        File ratesFile = new File(path);
        ArrayList<String> list = new ArrayList<>();
        Scanner scanner = new Scanner(ratesFile);
        while (scanner.hasNextLine()){
            list.add(scanner.nextLine());
        }
        return list;
    }

    private Kompuuter loeArvuti(String kirje) throws Formaadierind, IOException {
        if(!kirje.contains(";")||!kirje.contains("|")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(1,kirje);
        }
        String[] kirjeJaAeg = kirje.split("\\|");
        String info = kirjeJaAeg[0];
        String aeg = kirjeJaAeg[1];
        java.time.LocalDateTime aegVormingus = null;

        try{
            aegVormingus = java.time.LocalDateTime.parse(aeg);
        }
        catch (Exception e){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(4,kirje);
        }
        String[] infoEraldi = info.split(";");
        if ((infoEraldi.length!= 2 || infoEraldi.length!= 3) && !infoEraldi[1].equals("tavatöö")&& !infoEraldi[1].equals("kiirtöö")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(2,kirje);
        }
        else if(infoEraldi.length==3 && !infoEraldi[2].equals("monitoriga")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(3,kirje);
        }

        boolean kiirtöö = true;
        if(infoEraldi[1].equals("tavatöö")){
            kiirtöö = false;
        }

        if(infoEraldi.length==2){
            return (new Arvuti(infoEraldi[0],kiirtöö,aegVormingus));
        }
        return (new VäliseMonitorigaArvuti(infoEraldi[0],kiirtöö,aegVormingus));
    }

    private Kompuuter loeArvutiKasutajalt(String kirje) throws Formaadierind, IOException {

        if(!kirje.contains(";")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(1,kirje);
        }


        String[] infoEraldi = kirje.split(";");
        if ((infoEraldi.length!= 2 || infoEraldi.length!= 3) && !infoEraldi[1].equals("tavatöö")&& !infoEraldi[1].equals("kiirtöö")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(2,kirje);
        }
        else if(infoEraldi.length==3 && !infoEraldi[2].equals("monitoriga")){
            this.sisestaViganeKirje(kirje);
            throw new Formaadierind(3,kirje);
        }

        boolean kiirtöö = true;
        if(infoEraldi[1].equals("tavatöö")){
            kiirtöö = false;
        }

        if(infoEraldi.length==2){
            return (new Arvuti(infoEraldi[0],kiirtöö,LocalDateTime.now()));
        }
        return (new VäliseMonitorigaArvuti(infoEraldi[0],kiirtöö,LocalDateTime.now()));
    }

    public void lisaSummale(double raha){
        this.summa+=raha;
    }

    public void lisaLõpetatud(String tootja){
        if (parandatudInfo.get(tootja)==null){
            parandatudInfo.put(tootja,1);
        }
        else{
            parandatudInfo.put(tootja,parandatudInfo.get(tootja)+1);
        }
    }

    private void sisestaViganeKirje(String kirje) throws IOException {
        bw.write(kirje+"\n");
    }

    private void ootelArvutiLisamine() throws IOException{
        System.out.print("Sisesta kirjeldus:");
        String kirje = scanner.nextLine();
        try {
            this.parandamataArvutid.add(this.loeArvutiKasutajalt(kirje));
        } catch (Formaadierind formaadierind) {
            System.out.println("Vigane sisestus! Viga "+formaadierind.getViganeIndex()+". väljas.");
            ootelArvutiLisamine();
        }

    }

    public static HashMap<String, Double> loeTasud(String fileName) throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(fileName));
        HashMap<String, Double> andmed = new HashMap<>();

        int arv = input.readInt();
        for (int i = 0; i < arv; i++) {
            String nimi = input.readUTF();
            Double taks = input.readDouble();
            andmed.put(nimi,taks);
        }
        input.close();
        return andmed;
    }

    public void parandaArvuti(){
        if(parandamataArvutid.size()==0){
            System.out.println("Ootel arvutid puuduvad.");
        }
        else {
            Collections.sort(parandamataArvutid);
            Collections.reverse(parandamataArvutid);

            Kompuuter arvuti = parandamataArvutid.get(0);
            System.out.println("Arvuti info: " + arvuti.toString());
            Double tunnitasu = this.nimeKüsimineHinnaSaamine();
            int tööminutid = this.tööMinutiteKüsimine();

            arvuti.lõpetaTöö(tööminutid, tunnitasu, this);

            this.parandamataArvutid.remove(arvuti);
            this.parandatudArvutid.add(arvuti);

            parandatudArvutiteArv += 1;
        }
    }



    public Double nimeKüsimineHinnaSaamine(){
        System.out.print("Sinu nimi: ");
        String nimi = scanner.nextLine();
        if (hinnakiri.get(nimi)==null){
            System.out.println("Sellist nime andmebaasis pole. Proovi uuesti!");
            return nimeKüsimineHinnaSaamine();
        }
        else{
            return hinnakiri.get(nimi);
        }
    }

    public int tööMinutiteKüsimine(){
        System.out.print("Tööminutite arv: ");
        String minutid = scanner.nextLine();
        try{
            return Integer.parseInt(minutid);
        }
        catch(Exception e){
            System.out.println("Ei suutnud sinu sisendit numbriks töödelda. Proovi uuesti!");
            return tööMinutiteKüsimine();
        }
    }

    public boolean tööLõpetamine() throws IOException {
        System.out.println("Parandati "+parandatudArvutiteArv+" arvutit.");
        System.out.println("Töö info:");
        for (Map.Entry<String, Integer> entry : parandatudInfo.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() +"tk");
        }

        System.out.println("Ootele jäi "+parandamataArvutid.size()+" arvutit.");



        File tehtud = new File("tehtud.dat"); // tehtud faili kirjutamine
        tehtud.createNewFile();
        DataOutputStream output = new DataOutputStream(new FileOutputStream(tehtud));
        output.writeInt(parandatudArvutiteArv);
        for (Kompuuter kompuuter:parandatudArvutid) {
            output.writeUTF(kompuuter.getTootja());
            output.writeUTF(kompuuter.getRegistreerimiseAeg().toString());
            output.writeDouble(kompuuter.getArveSumma());
        }
        output.close();

        File ootel = new File("ootel.txt");
        ootel.createNewFile();
        FileWriter fw = new FileWriter(ootel.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (Kompuuter kompuuter:parandamataArvutid) {
            bw.write(kompuuter.toString()+"\n");
        }
        bw.close();
        scanner.close();
        this.bw.close();
        return false;
    }

}
