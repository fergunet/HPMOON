package es.ugr.hpmoon.problems.featureselection.jmltools;

import java.util.ArrayList;
import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class HPMoonDatasetTools {	
	/**
	 * Generates a new dataset from the input data as a projection of some selected features according to mask
	 * The length of mask should match the number of attributes in the dataset.
	 * Otherwise, the smaller value between the number of features in the dataset and the
	 * length of the mask will be used as the maximum number of selectable features 
	 * @param data Input dataset
	 * @param mask Mask to select the features in the new dataset
	 * @return A new dataset containing only the features selected by the mask
	 */
	public static Dataset project (Dataset data, boolean [] mask)
	{
		/* Indexes of the attributes to be selected according to the mask */
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i=0 ; i<Math.min(data.noAttributes(), mask.length) ; i++)
			if (mask[i])
				indexes.add(i);
					
		/* Create an empty dataset */
		Dataset outData = new DefaultDataset();
		
		/* For every instance in the dataset */
		double [] selectedAttrs = new double[indexes.size()];
		for (Instance inputIns : data)
		{
			for (int j=0 ; j<selectedAttrs.length ; j++)
				selectedAttrs[j] = inputIns.value(indexes.get(j));
			
			outData.add(new DenseInstance(selectedAttrs));
		}

		/* Return a dataset containing only the selected features */
		return outData;
	}
}
