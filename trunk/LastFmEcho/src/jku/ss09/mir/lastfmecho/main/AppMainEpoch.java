package jku.ss09.mir.lastfmecho.main;

import java.util.List;

import jku.ss09.mir.lastfmecho.bo.MirArtist;
import jku.ss09.mir.lastfmecho.bo.MusicFileParser;
import jku.ss09.mir.lastfmecho.bo.feature.Feature;
import jku.ss09.mir.lastfmecho.bo.feature.FeatureFactory;
import jku.ss09.mir.lastfmecho.bo.similarity.DistanceSimilarityLastFMEpoch;
import jku.ss09.mir.lastfmecho.bo.util.MatrixUtils;
import jku.ss09.mir.lastfmecho.bo.visualization.MirArtistNetworkGraphVisualizer;
import static org.math.array.DoubleArray.*;
import static org.math.array.LinearAlgebra.*;

/**
 * 
 * @author doris
 *
 */

public class AppMainEpoch {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dirPath = System.getProperty("user.dir");
		System.out.println(dirPath);
		
		/**
		 * MIR MusicFileParser 
		 * fetches content from genre, artist files provided for the project
		 */
		MusicFileParser fileParser = new MusicFileParser();
		fileParser.run();

////		System.out.println("Genres: " + fileParser.getGenreSet().size());
////		for (MirGenre mirGenre : fileParser.getGenreSet()) {
////			System.out.println(mirGenre.getName());
////		for (MirArtist artist: mirGenre.getArtistList())
////				System.out.println("\t" + artist.getName());
//		}

	
		/**
		 * 1.) retrieve LastFM Albums for all artists and extract release dates (year) 
		 * 2.) calc similarities for all and 
		 * 3.) visualize them 
		 * 
		 */
		
		System.out.println("---------- LastFM Tag Epoch Retrieval based on TopAlbums ----------");
		List<MirArtist> artistList = fileParser.getArtistList();
		int idx = 1;
		for (MirArtist mirArtist : artistList) {
			System.out.print(mirArtist.getName() + ": " );
			// 1. this creates and calculates the feature and 
			// 2. adds it to the mirArtist
			Feature feature = FeatureFactory.createFeatureForArtist(FeatureFactory.FEATURE_EPOCH, mirArtist);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			mirArtist.addFeature(feature);
			System.out.println(idx + " Artist: " + mirArtist.getName() + " calcFeature " + feature.getName());
			idx++;
		}
		
		
		//calculate similarity between artists based on their mean year of album releases
		System.out.println("----------- now calculate similarities ----------------");
		DistanceSimilarityLastFMEpoch distSimilarity = new DistanceSimilarityLastFMEpoch(1, "DistanceSimilarity", artistList);
		if(distSimilarity.runCalc()){
			double[][] resultMatrix = distSimilarity.getResults();
			for(int i = 0; i < resultMatrix.length; i++) {
				String line = "";
				for (int j = 0; j < resultMatrix[i].length; j++) {
					line+= i + " :"+ resultMatrix[i][j] +"\t";
				}
				System.out.println(line);
			}
			
			MirArtistNetworkGraphVisualizer vis = new MirArtistNetworkGraphVisualizer(artistList,distSimilarity.getResults());
			vis.init();
		}
		

	}
}
