import java.util.ArrayList;
import java.security.Security;
import java.util.HashMap;

public class TeenyChain {

  public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
  public static ArrayList<Block> blockchain = new ArrayList<Block>();
  public static int difficulty = 3;
  public static Wallet wallet1;
  public static Wallet wallet2;
  public static Transaction genesisTransaction;
  public static float minimumTransaction = 0.1f;

  public static void main(String[] args) {
    /* Setup Bouncey castle as a security provider */
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    wallet1 = new Wallet();
    wallet2 = new Wallet();
    Wallet coinBase = new Wallet();
    
    // create genesis transaction, which sends 100 TeenyCoin to wallet1:
    genesisTransaction = new Transaction(coinBase.publicKey, wallet1.publicKey, 100f, null);
    genesisTransaction.generateSignature(coinBase.privateKey); // manually sign the genesis transaction
    genesisTransaction.transactionID = "0"; // manually set the transaction id
    genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, 
                                    genesisTransaction.value, genesisTransaction.transactionID)); // manually add the Transactions Output


    /*store our
    * first transaction in the
    * UTXOs list
    */
    UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));     System.out.println("Creating and Mining Genesis block... ");

    Block genesis = new Block("0");
    genesis.addTransaction(genesisTransaction);
    addBlock(genesis);

    // testing
    Block block1 = new Block(genesis.hash);
    System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
    System.out.println("\nWallet1 is Attempting to send funds (40) to Wallet2...");
    block1.addTransaction(wallet1.sendFunds(wallet2.publicKey, 40f));
    addBlock(block1);
    System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
    System.out.println("Wallet2's balance is: " + wallet2.getBalance());

    Block block2 = new Block(block1.hash);
    System.out.println("\nWallet1 Attempting to send more funds (1000) than it has...");
    block2.addTransaction(wallet1.sendFunds(wallet2.publicKey, 1000f));
    addBlock(block2);
    System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
    System.out.println("Wallet2's balance is: " + wallet2.getBalance());

    Block block3 = new Block(block2.hash);
    System.out.println("\nWallet2 is Attempting to send funds (20) to Wallet1...");
    block3.addTransaction(wallet2.sendFunds(wallet1.publicKey, 20));
    System.out.println("\nWallet1's balance is: " + wallet1.getBalance());
    System.out.println("Wallet2's balance is: " + wallet2.getBalance());

    isChainValid();

    /*
    blockchain.add(new Block("The first Block", "0"));
    System.out.println("Trying to Mine Block 1... ");
    blockchain.get(0).mineBlock(difficulty);

    blockchain.add(new Block("The second Block", blockchain.get(blockchain.size()-1).hash));
    System.out.println("Trying to Mine Block 2... ");
    blockchain.get(1).mineBlock(difficulty);

    blockchain.add(new Block("The third Block",  blockchain.get(blockchain.size()-1).hash));
    System.out.println("Trying to Mine Block 3... ");
    blockchain.get(2).mineBlock(difficulty);

    System.out.println("\nBlockchain is valid : " + isChainValid());

    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockchainJson);
    */
  }

  public static Boolean isChainValid(){
    Block currentBlock;
    Block previousBlock;
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
    tempUTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));

    for (int i = 1; i < blockchain.size(); i++) {
      currentBlock = blockchain.get(i);
      previousBlock = blockchain.get(i-1);
      
      if(!currentBlock.hash.equals(currentBlock.calculateHash())){
        System.out.println("Current Hashes not equal");
        return false;
      }
      
      if(!previousBlock.hash.equals(currentBlock.previousHash)){
        System.out.println("Previous Hashes not equal");
        return false;
      }

      if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)){
        System.out.println("This block hasn't been mined");
        return false;
      }
      TransactionOutput tempOutput;
      for (int t = 0; t < currentBlock.transactions.size(); t++) {
        Transaction currentTransaction = currentBlock.transactions.get(t);

        if (!currentTransaction.verifySignature()) {
          System.out.println("#Signature on Transaction(" + t + ") is Invalid");
          return false;
        }
        if (currentTransaction.getInputsValues() != currentTransaction.getOutputValues()) {
          System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
          return false;
        }

        for (TransactionInput input : currentTransaction.inputs) {
          tempOutput = tempUTXOs.get(input.transactionOutputID);

          if (tempOutput == null) {
            System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
            return false;
          }

          if (input.UTXO.value != tempOutput.value) {
            System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
            return false;
          }

          tempUTXOs.remove(input.transactionOutputID);
        }

        for (TransactionOutput output : currentTransaction.outputs) {
          tempUTXOs.put(output.id, output);
        }

        if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
          System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
          return false;
        }
        if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
          System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
          return false;
        }
      }
    }
    System.out.println("Blockchain is valid");
    return true;
  }

  public static void addBlock(Block newBlock){
    newBlock.mineBlock(difficulty);
    blockchain.add(newBlock);
  }
}
