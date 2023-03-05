import java.util.*;
import java.io.*;


// Refernce:: https://www.geeksforgeeks.org/ml-frequent-pattern-growth-algorithm/ 
public class fpGrowthAlgorithm 
{
    public static void main(String[] args) throws Exception{
		// Scanner sc = new Scanner(new File("fpInput.txt"));
        Scanner sc = new Scanner(System.in);
		System.out.println("enter the no of transactions to be recorded : ");
		int t_no = sc.nextInt();
		System.out.println("Enter the transactions with comma seperated items :");
		String[] transactions = new String[t_no];
		for (int i = 0; i < t_no; i++) {
			transactions[i] = sc.next();
		}
		System.out.println("Enter the minimum support:");
		int minSupport = sc.nextInt();
		
		
        List<List<String>> transactionList = generateTransactionList(transactions);
        List<String> uniqueItems = generateUniqueItems(transactionList);
        HashMap<String,Integer> hm = generateHashMap(transactionList, uniqueItems);
        
        List<String> minSupportSatisfiedUniqueItems = uniqueItems
                                                        .stream()
                                                        .filter(s -> hm.get(s) >= minSupport)
                                                        .toList();


        FPTree fpTree = generateFPTree(transactionList, hm, minSupport);

        // System.out.println(generateSubSets(Arrays.asList(new String[] {"a", "b", "c"})));
        fpTree.printFullTree();

        HashMap<String, List<List<String>>> conditionalPatternBase = generateConditionalPatternBase(minSupportSatisfiedUniqueItems, fpTree);

        // System.out.println(conditionalPatternBase);

        HashMap<String, HashMap<List<String>, Integer>> conditionalFrequentPatternTree = new HashMap<String, HashMap<List<String>, Integer>>();
        
        HashMap<String, HashMap<List<String>, Integer>> frequentPatterns = new HashMap<String, HashMap<List<String>, Integer>>();
        for(String item: minSupportSatisfiedUniqueItems){
            List<List<String>> itemTransactionList = conditionalPatternBase.get(item);
            
            List<String> itemUniqueItems = generateUniqueItems(itemTransactionList);
            HashMap<String, Integer> itemHm = generateHashMap(itemTransactionList, itemUniqueItems);
            FPTree itemFPTree = generateFPTree(itemTransactionList, itemHm, minSupport);
            // itemFPTree.printFullTree();
            List<String> itemMinSupportSatisfiedUniqueItems = itemUniqueItems
                                                                .stream()
                                                                .filter(s-> itemHm.get(s) >= minSupport)
                                                                .toList();
            
            // System.out.println(itemMinSupportSatisfiedUniqueItems);
            for(String i:  itemMinSupportSatisfiedUniqueItems){
                // System.out.print(i + "::");
                // System.out.println(itemHm.get(i));
            }

            List<FPNode> itemNodes = itemFPTree.getNodesWithName(item);

            for(FPNode itemNode: itemNodes){
                List<String> itemAncestorNames = itemFPTree.getAncestorNames(itemNode);
                int itemFreq = itemNode.getFrequency();
                String itemName = itemNode.getItem();
                List<List<String>> subsets = generateSubSets(itemAncestorNames);

                HashMap<List<String>, Integer> tmpHm = new HashMap<List<String>, Integer>();
                    
                for(List<String> subset: subsets){
                    subset.add(itemName);
                    tmpHm.put(subset, itemFreq);
                }
                frequentPatterns.put(item, tmpHm);                }
                
            }

            
            
            // conditionalFrequentPatternTree.put(item, )
                                                                // System.out.print("Item " + item);
            // System.out.println(itemMinSupportSatisfiedUniqueItems);
        printFrequentPattern(frequentPatterns);

        sc.close();        
    }


    public static List<List<String>> generateTransactionList(String[] transactions){
        // List<String> uniqueItems = new ArrayList<>();
		List<List<String>> transactionList = new ArrayList<>();
        int t_no = transactions.length;
		for (int ptr = 0; ptr < t_no; ptr++) {
			String[] splitted = transactions[ptr].split(",");
			List<String> oneTransaction = new ArrayList<String>();
			for (String item : splitted) {
				oneTransaction.add(item);
				
			}
			transactionList.add((oneTransaction));
		}

        return transactionList;
    }

    public static List<String> generateUniqueItems(List<List<String>> transactionList){
        List<String> uniqueItems = new ArrayList<>();

        int t_no = transactionList.size();
		for (int ptr = 0; ptr < t_no; ptr++) {
			
            List<String> transaction = transactionList.get(ptr);
			for (String item : transaction) {
				
				if (uniqueItems.contains(item) == false) {
					uniqueItems.add(item);
				}
			}
		}

        return uniqueItems;
    }

    public static HashMap<String, Integer> generateHashMap(List<List<String>> transactionList, List<String> uniqueItems){
        // List<String> uniqueItems = generateUniqueItems(transactionList);
        
        
        HashMap<String, Integer> hm = new HashMap<>();
        for( String item:uniqueItems)
        {
            hm.put(item,0);
        }
        for(Map.Entry<String,Integer> item:hm.entrySet())
        {
            int count=0;
            for(List<String> onetrans :transactionList )
            {
                if(onetrans.contains(item.getKey()))
                {
                    count++;
                }
            }
            hm.replace(item.getKey(),count);
        }
        // System.out.println(hm);
        return hm;

    }
    
    private static void printFrequentPattern(HashMap<String, HashMap<List<String>, Integer>> frequentPatterns) {
        System.out.println("\n\nPrinting Frequent Patterns");
        for(Map.Entry<String,HashMap<List<String>,Integer>> entry: frequentPatterns.entrySet()){
            System.out.println("Item:: " + entry.getKey() + " Frequent Patterns");
            for(Map.Entry<List<String>, Integer> entry2: entry.getValue().entrySet()){
                System.out.print("\t");
                System.out.print(entry2.getKey() + ":: ");
                System.out.println(entry2.getValue());
            }
            
            
        }
    }

    private static List<List<String>> generateSubSets(List<String> set){
        List<List<String>> ans = new ArrayList<List<String>>();
        recursiveSubSetsGenerator(ans, set, new ArrayList<String>(), 0);
        return ans;
    }

    private static void recursiveSubSetsGenerator(List<List<String>> ans, List<String> set, ArrayList<String> output,
            int i) {
                
                if(i==set.size()){
                    if(output.size()==0){
                        return;
                    }
                    ans.add(output);
                    return;
                }

                recursiveSubSetsGenerator(ans, set, new ArrayList<>(output), i+1);
                output.add(set.get(i));
                recursiveSubSetsGenerator(ans, set, new ArrayList<>(output), i+1);
    }

    private static HashMap<String, List<List<String>>> generateConditionalPatternBase(List<String> uniqueItems,
            FPTree fpTree) {
                HashMap<String, List<List<String>>> ans = new HashMap<String, List<List<String>>>();

                for(String item: uniqueItems){


                    List<FPNode> nodes = fpTree.getNodesWithName(item);
                    List<List<String>> currentItemTransactionList = new ArrayList<List<String>>();
                    
                    for(FPNode node: nodes){
                        int freq = node.getFrequency();
                        List<String> ancestorNames = fpTree.getAncestorNames(node);
                        ancestorNames.add(node.getItem());
                        for(int i = 0; i < freq; i++){
                            currentItemTransactionList.add(ancestorNames);
                        }
                    }

                    ans.put(item, currentItemTransactionList);
        
                }
                return ans;
    }

    private static FPTree generateFPTree(List<List<String>> transactionList,
            HashMap<String, Integer> hm, int minSupport) {
        FPNode root = new FPNode("Null", 0, null);

        for(List<String> transaction: transactionList){
            List<String> sortedTransactions = transaction
                                                        .stream()
                                                        .filter(s -> hm.get(s) >= minSupport)
                                                        .sorted(new Comparator<String>(){
                                                            public int compare(String a, String b){
                                                                return hm.get(b) - hm.get(a);
                                                            }
                                                        })
                                                        .toList();
            
            // System.out.println(transaction);
            // System.out.println(sortedTransactions);
            FPNode curr = root;
            // System.out.println(curr);
            for(String item: sortedTransactions){
                FPNode candidate = curr.getChildWithName(item);
                if(candidate != null){
                    candidate.incrementFrequency();
                    // curr = candidate;
                }
                else{
                    candidate = new FPNode(item, 1, curr);
                    curr.addChild(candidate);
                    
                }
                curr = candidate;
            }

        }

        return new FPTree(root);

    }


    
}


class FPNode {
    private String item;
    private int frequency;
    private FPNode parent;
    private List<FPNode> children;

    public FPNode(String item, int frequency, FPNode parent) {
        this.item = item;
        this.frequency = frequency;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public String toString(){
        String result = "";
        result += ("Name:: " + item + " | ");
        result += ("Frequency:: " + frequency);
        // System.out.println("ParentName:: " + parent.getItem());
        return result;
    }

    public FPNode getChildWithName(String name){
        for(FPNode child: children){
            if(child==null){
                continue;
            }
            if(child.getItem().equals(name)){
                return child;
            }
        }
        return null;
    }

    public String getItem() {
        return item;
    }

    public int getFrequency() {
        return frequency;
    }

    public FPNode getParent() {
        return parent;
    }

    public List<FPNode> getChildren() {
        return children;
    }

    public void incrementFrequency() {
        frequency++;
    }

    public void addChild(FPNode child) {
        children.add(child);
    }
}

class FPTree {
    private FPNode root;
    
    public void minePatterns(){

    }

    public List<FPNode> getLeaves(){
        List<FPNode> ans = new ArrayList<FPNode>();

        // HashSet<FPNode> visited = new HashSet<FPNode>();
        LinkedList<FPNode> queue = new LinkedList<>();
        
        queue.addLast(root);

        while(queue.size() != 0){
            FPNode curr = queue.removeFirst();

            if(curr.getChildren().size()==0){
                ans.add(curr);
            }
            else{
                List<FPNode> children = curr.getChildren();
                for(FPNode child : children){
                    queue.addLast(child);
                }
            }
            
        }


        return ans;
    }

    public List<FPNode> getNodesWithName(String name){
        // FPNode curr = root;
        List<FPNode> ans = new ArrayList<FPNode>();

        // HashSet<FPNode> visited = new HashSet<FPNode>();
        LinkedList<FPNode> queue = new LinkedList<>();
        
        queue.addLast(root);

        while(queue.size() != 0){
            FPNode curr = queue.removeFirst();

            if(curr.item.equals(name)){
                ans.add(curr);
            }
            else{
                List<FPNode> children = curr.getChildren();
                for(FPNode child : children){
                    queue.addLast(child);
                }
            }
            
        }


        return ans;
    }

    public FPTree(FPNode root) {
        this.root = root;
    }

    public FPNode getRoot() {
        return root;
    }


    public void printFullTree() {
        printTreeFromNode(root, "");
    }

    private void printTreeFromNode(FPNode node, String indent) {
        System.out.println(indent + node.getItem() + "::" + node.getFrequency());

        for (FPNode child : node.getChildren()) {
            printTreeFromNode(child, indent + "\t");
        }
    }

    public List<String> getAncestorNames(FPNode node) {
        List<String> ans = new ArrayList<String>();

        FPNode curr = node.getParent();
        while(!curr.getItem().equals("Null")){
            ans.add(curr.getItem());
            curr = curr.getParent();
        }

        return ans;
    }
}


