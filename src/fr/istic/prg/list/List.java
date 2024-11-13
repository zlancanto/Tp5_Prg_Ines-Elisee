package fr.istic.prg.list;

import fr.istic.prg.list_util.Iterator;
import fr.istic.prg.list_util.SuperT;

/**
 * Liste en double chaînage par références
 * 
 * @author Zlanca-Nto ELisée MIHAN<zlanca-nto.mihan@etudiant.univ-rennes.fr>
 * @author Ines Gaetan NOUBI-SI KUISSEU
 *         <ines-gaetan.noubi@etudiant.univ-rennes.fr>
 * 
 *         Version corrigée et instrumentée (compte du nombre d'opérations).
 *         Utilise un versionnage pour empêcher les modifs concurrentes.
 * 
 * @param <T> : le type de valeurs stockées
 */
public class List<T extends SuperT<T>> {

    /**
     * Element de la liste
     */
    private class Element {
        private T value;
        private Element left;
        private Element right;

        public Element() {
            value = null;
            left = null;
            right = null;
        }
    }

    /**
     * Itérateur sur la liste
     */
    public class ListIterator implements Iterator<T> {

        /** L'élément de la liste pointé par l'itérateur */
        private Element current;

        /**
         * Contructeur privé. Place l'itérateur sur la tête de la liste.
         * L'itérateur doit être construit par la liste
         * 
         * @see List::iterator()
         */
        private ListIterator() {
            current = flag.right;
        }

        @Override
        public void goForward() {
            current = current.right;
        }

        @Override
        public void goBackward() {
            current = current.left;
        }

        @Override
        public void restart() {
            current = flag.right;
        }

        @Override
        public boolean isOnFlag() {
            return current == flag;
        }

        @Override
        public void remove() {
            assert current != flag : "Impossible de retirer le drapeau";
            Element leftNeighboor = current.left;
            Element rightNeighboor = current.right;
            leftNeighboor.right = rightNeighboor;
            rightNeighboor.left = leftNeighboor;
            current = rightNeighboor;
        }

        @Override
        public T getValue() {
            return current.value;
        }

        @Override
        public T nextValue() {
            this.goForward();
            return current.value;
        }

        @Override
        public void addLeft(T value) {
            Element newElement = new Element();
            newElement.value = value;
            newElement.left = current.left;
            newElement.right = current;
            current.left = newElement;
            newElement.left.right = newElement;
            current = current.left;
        }

        @Override
        public void addRight(T value) {
            Element newElement = new Element();
            newElement.value = value;
            newElement.left = current;
            newElement.right = current.right;
            current.right = newElement;
            newElement.right.left = newElement;
            current = current.right;
        }

        @Override
        public void setValue(T value) {
            current.value = value;
        }

        @Override
        public String toString() {
            return "Iterateur de liste : pas d'affichage possible \n";
        }

    } // class ListIterator

    /** Le drapeau (sentinelle) */
    private Element flag;

    /**
     * Constructeur
     * 
     * Instancie une liste vide (contenant seulement le drapeau).
     */
    public List() {
        flag = new Element();
        flag.left = flag;
        flag.right = flag;
    }

    /**
     * @return un itérateur sur la liste, l’élément courant de l’itérateur est
     *         positionné sur l’élément de tête.
     */
    public ListIterator iterator() {
        return new ListIterator();
    }

    /**
     * @return true si la liste est vide, false sinon
     */
    public boolean isEmpty() {
        return (flag.left == flag && flag.right == flag);
    }

    /**
     * Supprimer toutes les valeurs de la liste.
     */
    public void clear() {
        ListIterator it = this.iterator();
        while (!it.isOnFlag()) {
            it.remove();
            it.goForward();
        }
    }

    /**
     * Affecter la valeur v au drapeau.
     * 
     * @param v valeur à mettre dans le drapeau.
     */
    public void setFlag(T v) {
        flag.value = v;
    }

    /**
     * Ajouter v en tête de la liste.
     * 
     * @param v valeur à ajouter
     */
    public void addHead(T v) {
        ListIterator it = this.iterator();
        it.addLeft(v);
    }

    /**
     * Ajouter v en queue de la liste.
     * 
     * @param v valeur à ajouter
     */
    public void addTail(T v) {
        ListIterator it = iterator();
        it.goBackward();
        it.addLeft(v);
    }

    /**
     * @return une copie profonde de la liste this.
     */
    public List<T> copyOf() {
        List<T> nouvelleListe = new List<>();
        ListIterator it = iterator();
        while (!it.isOnFlag()) {
            nouvelleListe.addTail(it.getValue().copyOf());
            // UNE COPIE EST NECESSAIRE !!!
            it.goForward();
        }
        return nouvelleListe;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("contenu de la liste : \n");
        ListIterator it = iterator();
        while (!it.isOnFlag()) {
            result.append(it.getValue().toString());
            result.append(" ");
            it.goForward();
        }
        return result.toString();
    }

}