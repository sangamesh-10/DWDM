import java.util.*;

public class Apriori {
	public static void generateCombinations(List<String> elements, List<String> currentCombination, int numElements,
			List<List<String>> combinations) {
		if (numElements == 0) {
			combinations.add(new ArrayList<>(currentCombination));
			return;
		}
		for (int i = 0; i < elements.size(); i++) {
			String element = elements.get(i);
			currentCombination.add(element);
			generateCombinations(elements.subList(i + 1, elements.size()), currentCombination, numElements - 1,
					combinations);
			currentCombination.remove(currentCombination.size() - 1);
		}
	}

	public static HashMap<Set<String>, Integer> generateCombinations(List<String> elements, int numElements) {
		List<List<String>> combinations = new ArrayList<>();
		generateCombinations(elements, new ArrayList<>(), numElements, combinations);
		HashMap<Set<String>, Integer> hm = new HashMap<>();
		for (List<String> combination : combinations) {
			hm.put(new HashSet<String>(combination), 0);
		}
		return hm;
	}

	public static HashMap<Set<String>, Integer> Scan(List<Set<String>> transactions,
			HashMap<Set<String>, Integer> combinations, int min_support) {
		List<Set<String>> keysToUpdate = new ArrayList<>();
		for (Map.Entry<Set<String>, Integer> singleCombinationMap : combinations.entrySet()) {
			Set<String> singleCombination = singleCombinationMap.getKey();
			for (Set<String> transaction : transactions) {
				if (transaction.containsAll(singleCombination)) {
					keysToUpdate.add(singleCombination);

				}
			}
		}
		for (Set<String> key : keysToUpdate) {
			combinations.replace(key, combinations.get(key) + 1);
		}
		keysToUpdate.clear();
		for (Map.Entry<Set<String>, Integer> singleCombinationMap : combinations.entrySet()) {
			if (singleCombinationMap.getValue() < min_support) {
				keysToUpdate.add(singleCombinationMap.getKey());
			}
		}
		for (Set<String> key : keysToUpdate) {
			combinations.remove(key);
		}
		return combinations;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter the no of transactions to be recorded : ");
		int t_no = sc.nextInt();
		System.out.println("Enter the transactions with comma seperated items :");
		String[] transactions = new String[t_no];
		for (int i = 0; i < t_no; i++) {
			transactions[i] = sc.next();
		}
		System.out.println("Enter the minimum support:");
		int min_support = sc.nextInt();
		List<String> unique_items = new ArrayList<>();
		List<Set<String>> transactionSet = new ArrayList();
		for (int ptr = 0; ptr < t_no; ptr++) {
			String[] splitted = transactions[ptr].split(",");
			List<String> oneTransaction = new ArrayList<String>();
			for (String item : splitted) {
				oneTransaction.add(item);
				if (unique_items.contains(item) == false) {
					unique_items.add(item);
				}
			}
			transactionSet.add(new HashSet<String>(oneTransaction));
		}
		unique_items.sort(null);
		HashMap<Set<String>, Integer> combinations = new HashMap<>();
		HashMap<Set<String>, Integer> scanOutput = new HashMap<>();
		int scanCount = 1;
		boolean flag = true;
		while (flag) {
			combinations = generateCombinations(unique_items, scanCount);
			scanOutput = Scan(transactionSet, combinations, min_support);
			if (scanOutput.size() != 0) {
				System.out.println("---LIST AFTER SCAN - " + scanCount + " ---");
				System.out.println(scanOutput);
				scanCount++;
				continue;
			}
			if (scanOutput.size() == 0) {
				scanCount--;
				combinations = generateCombinations(unique_items, scanCount);
				scanOutput = Scan(transactionSet, combinations, min_support);
				System.out.println("--FINAL LIST--");
				System.out.println(scanOutput);
				break;
			}
		}
		sc.close();
	}
}