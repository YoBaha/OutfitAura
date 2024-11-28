import UIKit
import GoogleSignIn

//@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var googleSignInConfig: GIDConfiguration?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        if let clientID = Bundle.main.object(forInfoDictionaryKey: "GIDClientID") as? String {
            print("GIDClientID found: \(clientID)")
            googleSignInConfig = GIDConfiguration(clientID: clientID)
        } else {
            print("GIDClientID is not found in Info.plistT")
        }
        return true
    }

    func application(
        _ application: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
}
