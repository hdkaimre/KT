public class Formaadierind extends Exception {
    String viganeRida;
    int viganeIndex;

    Formaadierind(int viganeIndex, String viganeRida){
        this.viganeIndex = viganeIndex;
        this.viganeRida = viganeRida;
    }

    int getViganeIndex(){
        return viganeIndex;
    }

    String getViganeRida(){
        return viganeRida;
    }



}