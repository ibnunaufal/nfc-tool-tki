
# NFC Tools TKI

NFC Tools TKI is a tool used to read NFC cards based on PT TKI's rules.

## How to use


### 1. Add Repositories

Add this following code:

```

maven { url 'https://jitpack.io' }

```

`settings.gradle` Will be looks like this

```
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}

```


### 2. Add the Dependency

Add this dependency

```

  implementation 'com.github.ibnunaufal:nfc-tool-tki:1.7'

```
`app/build.gradle` will be looks like this

```
dependencies {
  implementation 'com.github.ibnunaufal:nfc-tool-tki:1.7'
}
```

### 3. Implement NFC Reading

For every activity that needs to read NFC, inherit instance from `ClassNfc()`. To get the tapped nfcId, observe livedata from `ClassNfcViewModel` that represent current detected nfc id. Check this example below:

```
class CardCheckActivity : ClassNfc() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        classNfcViewModel.nfcValue.observe(this) {
            Log.d("classNfcViewModel", "detected nfc: $it")
        }
    }

}
```

### 4. Implement NFC Writing

For every activity that needs to read NFC, inherit instance from `ClassNfc()`. For example we want to write "Hello" into a nfc card, we can use `setStringToWrite` from `ClassNfcViewModel`. 
Whenever string to write is set, the app will automatically write the string to the nfc card when the card is tapped.
Check this example below:

```
class CardCheckActivity : ClassNfc() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        classNfcViewModel.setStringToWrite("Hello")
        // then tap the nfc card, you can give a toast or something to notify the user to tap the card
    }

}
```
### <span style="color:red">Additional Note when writing NFC</span>
When stringToWrite variable on `ClassNfcViewModel` is set (not empty), whatever nfc card tapped will be written with the string. To avoid this, make sure to set the stringToWrite to empty string after the nfc card is tapped or we want to cancel the writing process.




### Additional Configuration
If the app need to connect & read nfc from `Bluetooth Nfc Reader` device from tki, do this on `onCreate` of activity that currently used.
```
classNfcViewModel.setSelectedBluetoothDeviceAddress(btDeviceAddress)
```
`btDeviceAddress` refers to `Bluetooth Nfc Reader` bluetooth address, that have format like "00:00:00:00:00:00".


#### How to get the bluetooth address?
Here is it the example of dialog showing bluetooth devices list, to get the exact bluetooth address just get `device.address` after device selected.

```
private fun showBluetoothDeviceDialog() {
        if (
            (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }
        val bondedDevices = bluetoothAdapter?.bondedDevices
        val deviceNames = bondedDevices?.map { it.name }?.toTypedArray()

        if (deviceNames != null && deviceNames.isNotEmpty()) {
            AlertDialog.Builder(this).apply {
                setTitle("Select Bluetooth Device")
                setItems(deviceNames) { dialog, which ->
                    val device = bondedDevices.elementAt(which)
                    
                    /// here you can get the device.address
                }
                setNegativeButton("Cancel", null)
                show()
            }
        } else {
            Toast.makeText(this, "No bonded devices found", Toast.LENGTH_SHORT).show()
        }
    }
```

## Authors

- [@ibnunaufal](https://www.github.com/ibnunaufal)
