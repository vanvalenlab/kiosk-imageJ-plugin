# ![DeepCell Kiosk Banner](https://raw.githubusercontent.com/vanvalenlab/kiosk-console/master/docs/images/DeepCell_Kiosk_Banner.png)

[![Build Status](https://travis-ci.com/vanvalenlab/kiosk-imageJ-plugin.svg?branch=master)](https://travis-ci.com/vanvalenlab/kiosk-imageJ-plugin)
[![Coverage Status](https://coveralls.io/repos/github/vanvalenlab/kiosk-imageJ-plugin/badge.svg?branch=master)](https://coveralls.io/github/vanvalenlab/kiosk-imageJ-plugin?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](/LICENSE)

The `kiosk-imageJ-plugin` is a ImageJ 1.x plugin for easily processing images with an existing DeepCell Kiosk from within ImageJ.

## How to install

1. Download the JAR file.
2. Open ImageJ
3. Navigate to Plugins > Install...
4. Select the downloaded JAR file.
5. Install it into the `jars` directory.

## How to Run the plugin

1. Open an image in ImageJ.
2. Navigate to Plugins > DeepCell Kiosk > Submit Active Image.
3. Update custom options (especially the DeepCell Kiosk Host to your own Kiosk IP address).
4. Select the job type ("segmentation" is default). Please make sure your image is appropriate for the job.
5. Click OK to run the job.
6. Await your results! They should be automatically downloaded and opened.
