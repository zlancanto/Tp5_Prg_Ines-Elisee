package fr.istic.prg.list;

import java.io.FileInputStream;

import fr.istic.prg.list_util.Comparison;
import fr.istic.prg.list_util.Iterator;
import fr.istic.prg.list_util.List;
import fr.istic.prg.list_util.SmallSet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * @author Zlanca-Nto ELisée MIHAN<zlanca-nto.mihan@etudiant.univ-rennes.fr>
 * @author Ines Gaetan NOUBI-SI KUISSEU
 *         <ines-gaetan.noubi@etudiant.univ-rennes.fr>
 * @version 4.1
 * @since 2023-10-11
 */

public class MySet extends List<SubSet> {

	/**
	 * Borne supérieure pour les rangs des sous-ensembles.
	 */
	private static final int MAX_RANG = 128;
	/**
	 * Sous-ensemble de rang maximal à mettre dans le drapeau de la liste.
	 */
	private static final SubSet FLAG_VALUE = new SubSet(MAX_RANG, new SmallSet());

	private static final String NEW_VALUE = " nouveau contenu :"; // demande de SonarLint

	/**
	 * Entrée standard.
	 */
	private static final Scanner standardInput = new Scanner(System.in);

	public MySet() {
		super();
		setFlag(FLAG_VALUE);
	}

	/**
	 * Fermer tout (actuellement juste l'entrée standard).
	 */
	public static void closeAll() {
		standardInput.close();
	}

	private static Comparison compare(int a, int b) {
		if (a < b) {
			return Comparison.INF;
		} else if (a == b) {
			return Comparison.EGAL;
		} else {
			return Comparison.SUP;
		}
	}

	/**
	 * Afficher à l’écran les entiers appartenant à this, dix entiers par ligne
	 * d’écran.
	 */
	public void print() {
		System.out.println(" [version corrigee de contenu]");
		this.print(System.out);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// //////////// Appartenance, Ajout, Suppression, Cardinal
	// ////////////////////
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * @return true si le nombre saisi par l'utilisateur appartient à this, false
	 *         sinon
	 */
	public boolean contains() {
		System.out.println(" valeur cherchee : ");
		int value = readValue(standardInput, 0);
		return this.containsValue(value);
	}

	/**
	 * @param element valeur à tester
	 * @return true si valeur appartient à l'ensemble, false sinon
	 */

	public boolean containsValue(int value) {

		int rank = value / 256;
		int smallSetValue = value % 256;
		Iterator<SubSet> it = this.iterator();

		while (it.getValue().rank < rank) {
			it.goForward();
		}
		if (it.getValue().rank == rank && it.getValue().set.contains(smallSetValue)) {
			return true;
		}

		return false;
	}

	/**
	 * Ajouter à this toutes les valeurs saisies par l'utilisateur et afficher le
	 * nouveau contenu (arrêt par lecture de -1).
	 */
	public void add() {
		System.out.println(" valeurs a ajouter (-1 pour finir) : ");
		this.addAllFromStream(System.in);
		System.out.println(NEW_VALUE);
		this.printNewState();
	}

	/**
	 * Ajouter à this toutes les valeurs prises dans is.
	 * 
	 * @param is flux d'entrée.
	 */
	public void addAllFromStream(InputStream is) {
		try {
			/* Correspond à la valeur qui doit être ajoutée à this */
			int value;
			while ((value = is.read()) != -1) {
				addNumber(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ajouter element à this.
	 *
	 * @param element valuer à ajouter.
	 */
	public void addNumber(int value) {

		int rank = value / 256;
		int smallSetOccurence = value % 256;
		Iterator<SubSet> it = this.iterator();
		SubSet subSet = it.getValue();

		while (subSet.rank < rank) {
			it.goForward();
		}

		if (subSet.rank == rank) {
			subSet.set.add(smallSetOccurence);
		} else {
			SmallSet smallSet = new SmallSet();
			smallSet.add(smallSetOccurence);
			it.addLeft(new SubSet(rank, smallSet));
		}
	}

	/**
	 * Supprimer de this toutes les valeurs saisies par l'utilisateur et afficher le
	 * nouveau contenu (arrêt par lecture de -1).
	 */
	public void remove() {
		System.out.println("  valeurs a supprimer (-1 pour finir) : ");
		this.removeAllFromStream(System.in);
		this.printNewState();
	}

	/**
	 * Supprimer de this toutes les valeurs prises dans is.
	 * 
	 * @param is flux d'entrée
	 */
	public void removeAllFromStream(InputStream is) {
		try {
			/* Correspond à la valeur qui doit être supprimée de this */
			int value;
			while ((value = is.read()) != -1) {
				removeNumber(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Supprimer element de this.
	 * 
	 * @param element valeur à supprimer
	 */
	public void removeNumber(int value) {

		int rank = value / 256;
		int smallSetOccurence = value % 256;
		Iterator<SubSet> it = this.iterator();
		SubSet subSet = it.getValue();

		while (subSet.rank < rank) {
			it.goForward();
		}

		if (subSet.rank == rank) {
			subSet.set.remove(smallSetOccurence);
		}
	}

	/**
	 * @return taille de l'ensemble this
	 */
	public int size() {
		Iterator<SubSet> it = this.iterator();
		SubSet subSet = it.getValue();
		int count = 0;
		while (!it.isOnFlag()) {
			count += subSet.set.size();
			it.goForward();
		}
		return count;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////// Difference, DifferenceSymetrique, Intersection, Union ///////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * This devient la différence de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void difference(MySet set2) {

		/* Parcours this */
		Iterator<SubSet> it1 = this.iterator();

		/* Parcours set2 */
		Iterator<SubSet> it2 = set2.iterator();

		while (!it1.isOnFlag() && !it2.isOnFlag()) {
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();
			if (subSet1.rank < subSet2.rank) {
				it1.goForward();
			} else if (subSet1.rank > subSet2.rank) {
				it2.goForward();
			} else {
				it1.getValue().set.difference(subSet2.set);
			}
		}
	}

	/**
	 * This devient la différence symétrique de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void symmetricDifference(MySet set2) {
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		while (!it2.isOnFlag()) {
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();

			if (subSet1.rank < subSet2.rank) {
				// Garde subSet1 seulement, car il est unique à this
				it1.goForward();
			} else if (subSet1.rank > subSet2.rank) {
				// Ajoute subSet2, car il est unique à set2
				it1.addLeft(subSet2);
				it2.goForward();
			} else if (subSet1.rank == subSet2.rank) {
				it1.getValue().set.symmetricDifference(subSet2.set);
				if (!it1.getValue().set.isEmpty()) {
					it1.goForward();
				} else {
					// Supprime subSet1 si la différence symétrique est vide
					it1.remove();
				}
				it2.goForward();
			}
		}
	}

	/**
	 * This devient l'intersection de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void intersection(MySet set2) {

		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();
		while (!it1.isOnFlag()) {
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();
			if (subSet1.rank < subSet2.rank) {
				it1.remove();
			} else if (subSet1.rank > subSet2.rank) {
				it2.goForward();
			} else if (subSet1.rank == subSet2.rank) {
				it1.getValue().set.intersection(subSet2.set);
				if (it1.getValue().set.isEmpty()) {
					it1.remove();
				}
				it2.goForward();
			}
		}
	}

	/**
	 * This devient l'union de this et set2.
	 * 
	 * @param set2 deuxième ensemble
	 */
	public void union(MySet set2) {

		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		while (!it2.isOnFlag()) {
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();

			if (subSet1.rank < subSet2.rank) {
				it1.goForward();
			} else if (subSet1.rank > subSet2.rank) {
				it1.addLeft(subSet2);
				it1.goForward();
				it2.goForward();
			} else if (subSet1.rank == subSet2.rank) {
				it1.getValue().set.union(subSet2.set);
				it1.goForward();
				it2.goForward();
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////////////////// Egalit�, Inclusion ////////////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * @param o deuxième ensemble
	 * 
	 * @return true si les ensembles this et o sont égaux, false sinon
	 */
	@Override
	public boolean equals(Object o) {
		boolean b = true;
		if (this == o) {
			b = true;
		} else if (o == null) {
			b = false;
		} else if (!(o instanceof MySet)) {
			b = false;
		} else {
			MySet set2 = (MySet) o;
			Iterator<SubSet> it1 = this.iterator();
			Iterator<SubSet> it2 = set2.iterator();
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();

			while (!it1.isOnFlag() &&
					!it2.isOnFlag() &&
					subSet1.rank == subSet2.rank &&
					subSet1.set.equals(subSet2.set)) {

				subSet1 = it1.nextValue();
				subSet2 = it2.nextValue();
			}
			b = it1.isOnFlag() && it2.isOnFlag() ? true : false;
		}
		return b;
	}

	/**
	 * @param set2 deuxième ensemble
	 * @return true si this est inclus dans set2, false sinon
	 */
	public boolean isIncludedIn(MySet set2) {

		/* Parcours this */
		Iterator<SubSet> it1 = this.iterator();
		/* Parcours set2 */
		Iterator<SubSet> it2 = set2.iterator();

		while (!it1.isOnFlag()) {
			SubSet subSet1 = it1.getValue();
			SubSet subSet2 = it2.getValue();

			if ((subSet2.rank < subSet1.rank && subSet1.rank < it2.nextValue().rank)) {
				return false;
			} else if (subSet1.rank == subSet2.rank && !subSet1.set.isIncludedIn(subSet2.set)) {
				return false;
			} else if (subSet2.rank < subSet1.rank) {
				it2.goForward();
			} else if (subSet1.rank == subSet2.rank && subSet1.set.isIncludedIn(subSet2.set)) {
				it1.goForward();
				it2.goForward();
			}
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// //////// Rangs, Restauration, Sauvegarde, Affichage //////////////
	// /////////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		int i = 1;
		int result = 0;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			SubSet ss = it.getValue();
			result += i * ss.rank * ss.set.hashCode();
			i *= 31;
		}
		return result;
	}

	/**
	 * Afficher les rangs présents dans this.
	 */
	public void printRanks() {
		System.out.println(" [version corrigee de rangs]");
		this.printRanksAux();
	}

	private void printRanksAux() {
		int count = 0;
		Iterator<SubSet> it = this.iterator();
		StringBuilder line = new StringBuilder("Rangs presents : ");
		while (!it.isOnFlag()) {
			line.append(it.getValue().rank + "  ");
			count = count + 1;
			if (count == 10) {
				line.append("\n");
				count = 0;
			}
			it.goForward();

		}
		System.out.println(line.toString());
		if (count > 0) {
			System.out.println("\n");
		}
	}

	/**
	 * Créer this à partir d’un fichier choisi par l’utilisateur contenant une
	 * séquence d’entiers positifs terminée par -1 (cf f0.ens, f1.ens, f2.ens,
	 * f3.ens et f4.ens).
	 */
	public void restore() {
		String fileName = readFileName();
		InputStream inFile;
		try {
			inFile = new FileInputStream(fileName);
			System.out.println(" [version corrigee de restauration]");
			this.clear();
			this.addAllFromStream(inFile);
			inFile.close();
			System.out.println(NEW_VALUE);
			this.printNewState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("fichier " + fileName + " inexistant");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier " + fileName);
		}
	}

	/**
	 * Sauvegarder this dans un fichier d’entiers positifs terminé par -1.
	 */
	public void save() {
		System.out.println(" [version corrigee de sauvegarde]");
		OutputStream outFile;
		try {
			outFile = new FileOutputStream(readFileName());
			this.print(outFile);
			outFile.write("-1\n".getBytes());
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("pb ouverture fichier lors de la sauvegarde");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier");
		}
	}

	/**
	 * @return l'ensemble this sous forme de chaîne de caractères.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		SubSet subSet;
		int startValue;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			subSet = it.getValue();
			startValue = subSet.rank * 256;
			for (int i = 0; i < 256; ++i) {
				if (subSet.set.contains(i)) {
					StringBuilder number = new StringBuilder(String.valueOf(startValue + i));
					int numberLength = number.length();
					for (int j = 6; j > numberLength; --j) {
						number.append(" ");
					}
					result.append(number);
					++count;
					if (count == 10) {
						result.append("\n");
						count = 0;
					}
				}
			}
			it.goForward();
		}
		if (count > 0) {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Imprimer this dans outFile.
	 *
	 * @param outFile flux de sortie
	 */
	private void print(OutputStream outFile) {
		try {
			String string = this.toString();
			outFile.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Afficher l'ensemble avec sa taille et les rangs présents.
	 */
	private void printNewState() {
		this.print(System.out);
		int size = this.size();
		System.out.println("Nombre d'elements : " + size);
		this.printRanksAux();
	}

	/**
	 * @param scanner
	 * @param min     valeur minimale possible
	 * @return l'entier lu au clavier (doit être entre min et 32767)
	 */
	private static int readValue(Scanner scanner, int min) {
		int value = scanner.nextInt();
		while (value < min || value > 32767) {
			System.out.println("valeur incorrecte");
			value = scanner.nextInt();
		}
		return value;
	}

	/**
	 * @return nom de fichier saisi psar l'utilisateur
	 */
	private static String readFileName() {
		System.out.println(" nom du fichier : ");
		return standardInput.next();
	}
}