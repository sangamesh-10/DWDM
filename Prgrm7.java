import java.util.*;
public class Prgrm7 
{
    private static class FPNode {
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
    private static class FPTree {
        private FPNode root;
        private Map<String, List<FPNode>> headerTable;

        public FPTree() {
            root = new FPNode(null, 0, null);
            headerTable = new HashMap<>();
        }

        public FPNode getRoot() {
            return root;
        }

        public Map<String, List<FPNode>> getHeaderTable() {
            return headerTable;
        }

        public void addTransaction(List<String> transaction) {
            FPNode currentNode = root;

            for (String item : transaction) {
                FPNode child = currentNode.getChildren()
                        .stream()
                        .filter(c -> c.getItem().equals(item))
                        .findFirst()
                        .orElse(null);

                if (child == null) {
                    child = new FPNode(item, 1, currentNode);
                    currentNode.addChild(child);

                    // Update header table
                    List<FPNode> nodes = headerTable.getOrDefault(item, new ArrayList<>());
                    nodes.add(child);
                    headerTable.put(item, nodes);
                } else {
                    child.incrementFrequency();
                }

                currentNode = child;
            }
        }
        public void print() {
            print(root, "");
        }

        private void print(FPNode node, String indent) {
            System.out.println(indent + node.getItem() + " " + node.getFrequency());

            for (FPNode child : node.getChildren()) {
                print(child, indent + " ");
            }
        }
    }
    public static LinkedHashMap<String, Integer> sortHashMapByValue(HashMap<String, Integer> hashmap) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(hashmap.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    public static List<List<String>> sortingtrans(List<List<String>> transactions,LinkedHashMap<String,Integer> lorder)
    {
        for(List items:transactions)
        {
            for(int i=0;i<items.size();i++)
            {
                for(int j=i+1;j<items.size();j++)
                {
                    if(lorder.get(items.get(i))< lorder.get(items.get(j)))
                    {
                        String t1=items.get(i).toString();
                        String t2=items.get(j).toString();
                        items.set(i, t2);
                        items.set(j,t1);
                    }
                }
            }
        }
        return transactions;
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
		List<List<String>> transactionList = new ArrayList();
		for (int ptr = 0; ptr < t_no; ptr++) {
			String[] splitted = transactions[ptr].split(",");
			List<String> oneTransaction = new ArrayList<String>();
			for (String item : splitted) {
				oneTransaction.add(item);
				if (unique_items.contains(item) == false) {
					unique_items.add(item);
				}
			}
			transactionList.add((oneTransaction));
		}
		unique_items.sort(null);
        HashMap<String,Integer> hm=new HashMap<>();
        for( String item:unique_items)
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
        System.out.print(hm);
        LinkedHashMap<String,Integer> lorder=sortHashMapByValue(hm);
        transactionList=sortingtrans(transactionList, lorder);
        System.out.println(transactionList);
        FPTree tree = new FPTree();
        for (List<String> transaction : transactionList) {
            tree.addTransaction(transaction);
        }
        List<List<String>> frequentItemSets = new ArrayList<>();
        List<String> prefix = new ArrayList<>();
        List<String> rLorder = new ArrayList(lorder.keySet());
        Collections.reverse(rLorder);
        mineFrequentItemSets(tree, min_support, frequentItemSets, prefix);
        // System.out.println(rLorder);
        // Print frequent item sets
        System.out.println(tree.getHeaderTable().keySet());
        // System.out.println("Frequent Item Sets (support >= " + min_support + "):");
        // for (List<String> itemSet : frequentItemSets) {
        //     System.out.println(itemSet.toString());
        // }
    }

    private static void mineFrequentItemSets(FPTree tree, int minSupport, List<List<String>> frequentItemSets, List<String> prefix) {
        // Generate conditional pattern base for each item in the header table
        for (String item : tree.getHeaderTable().keySet()) {
            List<FPNode> nodes = tree.getHeaderTable().get(item);
            int frequency = nodes.stream().mapToInt(FPNode::getFrequency).sum();
    
            // Check if item is frequent
            if (frequency < minSupport) {
                continue;
            }
    
            // Add frequent item set to result list
            List<String> itemSet = new ArrayList<>(prefix);
            itemSet.add(item);
            frequentItemSets.add(itemSet);
    
            // Generate conditional pattern base
            FPTree conditionalTree = new FPTree();
            for (FPNode node : nodes) {
                List<String> path = new ArrayList<>();
                FPNode currentNode = node;
    
                while (currentNode.getParent() != null) {
                    path.add(currentNode.getItem());
                    currentNode = currentNode.getParent();
                }
    
                Collections.reverse(path);
                conditionalTree.addTransaction(path.subList(1, path.size()));
            }
    
            // Recursively mine frequent item sets from conditional tree
            mineFrequentItemSets(conditionalTree, minSupport, frequentItemSets, itemSet);
        }
    }
}
