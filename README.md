# ![DeepCell Kiosk Banner](https://raw.githubusercontent.com/vanvalenlab/kiosk-console/master/docs/images/DeepCell_Kiosk_Banner.png)

[![Build Status](https://github.com/vanvalenlab/kiosk-imageJ-plugin/workflows/build/badge.svg)](https://github.com/vanvalenlab/kiosk-imageJ-plugin/actions)
[![Coverage Status](https://coveralls.io/repos/github/vanvalenlab/kiosk-imageJ-plugin/badge.svg?branch=master)](https://coveralls.io/github/vanvalenlab/kiosk-imageJ-plugin?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](/LICENSE)

The `kiosk-imageJ-plugin` is a ImageJ 1.x plugin for easily processing images with an existing DeepCell Kiosk from within ImageJ.

## How to install

1. Download the [latest JAR file](https://github.com/vanvalenlab/kiosk-imageJ-plugin/releases/download/0.3.2/Kiosk_ImageJ-0.3.2.jar).
2. Open ImageJ.
3. Navigate to Plugins > Install...
4. Select the downloaded JAR file.
5. Install it into the `plugins` folder.

## How to Run the plugin

1. Open an image in ImageJ.

![image](resources/step_1_screenshot.png) 

2. Navigate to Plugins > DeepCell Kiosk > Submit Active Image.  

![image](resources/step_2_screenshot.png) 

3. If you're running one of our pretrained models, please make sure the settings all match the screenshot below. If you're running your own Kiosk, make sure to update the IP address for your custom instance. 

![image](resources/step_3_screenshot.png) 

4. Select which model you wish to run ("multiplex" is the default). Please see our [documentation](https://github.com/vanvalenlab/intro-to-deepcell/tree/master/deepcell_dot_org#formatting-data-for-web-based-models) for data requirements for the different model types. 

![image](resources/step_4_screenshot.png) 

5. Click OK to run the job.
6. Await your results! They should be automatically downloaded and opened.

## QuPath Integration

[QuPath supports ImageJ](https://qupath.readthedocs.io/en/latest/docs/advanced/imagej.html) and this plugin can be used within the QuPath's internal ImageJ instance.

1. Configure the QuPath's [ImageJ plugin directory.](https://qupath.readthedocs.io/en/latest/docs/advanced/imagej.html#accessing-imagej-plugins)
2. Open the image to process in QuPath.
3. Send the image to ImageJ (Extensions/ImageJ/Send region to ImageJ).
4. Process the image normally.
5. Only overlays and ROIs can be sent back to QuPath. Create an Overlay from the resulting label image (Plugins/DeepCell Kiosk/Create Label Overlay).
6. Send the resulting overlay back to QuPath (Plugins/Send Overlay to QuPath).
