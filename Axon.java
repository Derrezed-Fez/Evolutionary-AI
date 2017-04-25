package ai;

public class Axon {

	public int layer, neuron;
	
	/* If you are wanting to know why I built my code this way. This is just a proof of concept.
	 * If you are wondering why the neurons don't have outputs, to send an neuron data upon completion
	 * can be problematic if the output neuron needs input from the other neurons, especially in the same layer.
	 * So instead of the input given the box of info to the output, the output takes the box from input when needed.
	 * That saved computational time and space for me. Again, this is just for fun (and most important, entertainment for my friends)
	 * 
	 */
	
	public Axon(int layer, int neuron) {
		this.layer  = layer;
		this.neuron = neuron;
	}
	
	/* Needed for remI() in Neuron.java */
	public boolean equals(Axon a)
	{
		return a.layer == this.layer && a.neuron == this.neuron;
	}

}
