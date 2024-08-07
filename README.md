
# NFC Tools TKI

NFC Tools TKI is a tool used to read NFC cards based on PT TKI's rules. Each time an NFC card is tapped, it reads the first 8 characters of the NFC ID.

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

## Authors

- [@ibnunaufal](https://www.github.com/ibnunaufal)
