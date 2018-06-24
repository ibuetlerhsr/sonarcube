import expect from 'expect';
import Enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-15';
import {extractNonTranslatableParts, fillTranslatedText, fillSectionTranslation, fillStepTranslation, parseJsonData} from './ExtractUtil';

Enzyme.configure({ adapter: new Adapter() });

test('Extract Non translatable Parts', () => {
    const text = '# Installation Hacking-Lab LiveCD\n' +
        '## Einleitung\n' +
        'Dieser Guide unterstützt Sie bei der Installation der Hacking-Lab LiveCD auf ihren Laptop oder Computer. Die LiveCD wird benötigt, um eine VPN Verbindung ins Hacking-Lab aufzubauen und basiert auf einem 64 Bit Kali Linux System. Darüber hinaus ist die LiveCD für diverse Übungen im Hacking-Lab optimiert und vorbereitet. Sie benötigen ein 64 Bit Laptop, damit die LiveCD korrekt funktioniert.\n' +
        '\n' +
        '\n' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```' +
        '## Pre-Requirement\n' +
        'Damit die LiveCD auf Ihrem Computer genutzt werden kann, muss die Hypervisor Funktion im BIOS von Ihrem PC aktiviert sein. Bitte stellen Sie dies zuerst sicher, ansonsten lässt sich die LiveCD nicht starten.\n' +
        '\n' +
        '![bios](bios.png)\n' +
        '\n' +
        '## Installation VirtualBox\n' +
        'Bitte laden Sie von http://www.virtualbox.org die neueste VirtualBox Software für Ihr Betriebssystem herunter. Folgen Sie der Installationsanleitung auf der Webseite. Für die Installation sind Administrator Rechte notwendig.\n' +
        '\n' +
        '## Download Hacking-Lab OVA\n' +
        'Bitte laden Sie von https://media.hacking-lab.com/installation/virtualbox/ die OVA Datei für Windows oder OSX herunter. Stellen Sie sicher, dass Sie die korrekte Version herunterladen. Prüfen Sie nach dem Download, dass die Datei die Dateiendung "ova" aufweist. Sollte sie "zip" sein, benennen Sie die Datei auf "ova" um.\n' +
        '\n' +
        '## Import LiveCD in VirtualBox\n' +
        'Bitte starten Sie VirtualBox und drücken Sie die Tasten "CTRL + I". Sie werden aufgefordert den Pfad anzugeben, wo sich die "ova" Datei befindet. Wählen Sie diese aus und bestätigen Sie die Eingabe. Sie können die Import Funktion auch via Menü über Datei -> Appliance Importieren starten. Nach ein paar Minuten "Import" steht Ihnen die LiveCD in VirtualBox zur Verfügung.\n' +
        '\n' +
        '## Starten LiveCD\n' +
        'Bitte starten Sie die LiveCD in VirtualBox. Es dauert ein paar Minuten und anschliessend werden Sie aufgefordert, einen Benutzername und Passwort einzugeben. Bitte verwenden Sie folgende Angaben:\n' +
        '\n' +
        'Benutzername = hacker\n' +
        'Passwort = compass\n' +
        '\n' +
        '![gui](gui.png)\n' +
        '\n' +
        'Nach dem ersten Login sehen Sie die graphische Oberfläche der Hacking-Lab LiveCD.\n' +
        '\n' +
        '\n' +
        '## Sprache der Tastatur\n' +
        'Die Tastatur in der LiveCD ist auf US English eingestellt. Sie können die Tastatur bequem über einen Menü Eintrag verändern. Beispielsweise für Schweizerdeutsch:\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Klicken Sie auf Change-Keyboard-Layout-Permanently\n' +
        '\n' +
        'Ein neues Fenster erscheint. Geben Sie dort "ch" ein und betätigen Sie die ENTER Taste. Nun ist ihre LiveCD auf Schweiz eingestellt, auch wenn Sie die LiveCD neu starten.\n' +
        '\n' +
        '![keyboard](keyboard.png)\n' +
        '\n' +
        '\n' +
        '## Updates LiveCD\n' +
        '\n' +
        'Bitte aktualisieren Sie Ihre LiveCD von Zeit zu Zeit. Hierfür gibt es einen Menü Eintrag, über welchen Sie die LiveCD aktualisieren können. Eine Internet Verbindung wird hierfür vorausgesetzt.\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Update LiveCD\n' +
        '\n' +
        '![update](update.png)\n' +
        '\n' +
        'Da Kali Linux viele Updates bringt, müssen Sie ca. 5-10 Minuten dafür einrechnen. Sie können aber auch während dem Update in Ihrer LiveCD weiter arbeiten.\n' +
        '\n' +
        '\n' +
        '## Mauszeiger\n' +
        'Drücken Sie die CTRL Taste auf der rechten Seite von Ihrer Tastatur, wenn Sie den Mauszeiger aus der VirtualBox befreien möchten.\n' +
        '\n' +
        '## Copy / Paste\n' +
        'Die LiveCD sollte so eingestellt sein, dass Sie mit Copy und Paste arbeiten können. Falls Sie Probleme mit Copy und Paste haben, so kann man die Funktion in VirtualBox gesondert freischalten. Bitte prüfen Sie die Einstellungen unter VirtualBox unter "Gerät" (Gemeinsame Zwischenablage) und (Drag und Drop)\n' +
        '\n' +
        '## VPN\n' +
        '\n' +
        '![vpn](vpn.png)\n' +
        '\n' +
        'Um auf die verwundbaren Systeme im Hacking-Lab zugreifen zu können, müssen Sie eine VPN Verbindung mit dem Hacking-Lab aufbauen. Sie sehen oben rechts im Menü ein VPN Icon. Bitte klicken Sie mit der rechten Maustaste auf das VPN Icon und wählen Sie "Connect Hacking-Lab". Sie müssen einen Benutzername und Passwort eingeben. Bitte registrieren Sie auf https://www.hacking-lab.com/ einen Account und verwenden Sie für das VPN den genau gleichen Benutzernamen (e-mail) und Ihr gewähltes Passwort.' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```';
    const output = extractNonTranslatableParts(text);
    expect(output.text).toEqual('# Installation Hacking-Lab LiveCD\n## Einleitung\nDieser Guide unterstützt Sie bei der Installation der Hacking-Lab LiveCD auf ihren Laptop oder Computer. Die LiveCD wird benötigt, um eine VPN Verbindung ins Hacking-Lab aufzubauen und basiert auf einem 64 Bit Kali Linux System. Darüber hinaus ist die LiveCD für diverse Übungen im Hacking-Lab optimiert und vorbereitet. Sie benötigen ein 64 Bit Laptop, damit die LiveCD korrekt funktioniert.\n\n\n-[0]-## Pre-Requirement\nDamit die LiveCD auf Ihrem Computer genutzt werden kann, muss die Hypervisor Funktion im BIOS von Ihrem PC aktiviert sein. Bitte stellen Sie dies zuerst sicher, ansonsten lässt sich die LiveCD nicht starten.\n\n![bios]-[2]-\n\n## Installation VirtualBox\nBitte laden Sie von -[9]- die neueste VirtualBox Software für Ihr Betriebssystem herunter. Folgen Sie der Installationsanleitung auf der Webseite. Für die Installation sind Administrator Rechte notwendig.\n\n## Download Hacking-Lab OVA\nBitte laden Sie von -[7]- die OVA Datei für Windows oder OSX herunter. Stellen Sie sicher, dass Sie die korrekte Version herunterladen. Prüfen Sie nach dem Download, dass die Datei die Dateiendung "ova" aufweist. Sollte sie "zip" sein, benennen Sie die Datei auf "ova" um.\n\n## Import LiveCD in VirtualBox\nBitte starten Sie VirtualBox und drücken Sie die Tasten "CTRL + I". Sie werden aufgefordert den Pfad anzugeben, wo sich die "ova" Datei befindet. Wählen Sie diese aus und bestätigen Sie die Eingabe. Sie können die Import Funktion auch via Menü über Datei -> Appliance Importieren starten. Nach ein paar Minuten "Import" steht Ihnen die LiveCD in VirtualBox zur Verfügung.\n\n## Starten LiveCD\nBitte starten Sie die LiveCD in VirtualBox. Es dauert ein paar Minuten und anschliessend werden Sie aufgefordert, einen Benutzername und Passwort einzugeben. Bitte verwenden Sie folgende Angaben:\n\nBenutzername = hacker\nPasswort = compass\n\n![gui]-[3]-\n\nNach dem ersten Login sehen Sie die graphische Oberfläche der Hacking-Lab LiveCD.\n\n\n## Sprache der Tastatur\nDie Tastatur in der LiveCD ist auf US English eingestellt. Sie können die Tastatur bequem über einen Menü Eintrag verändern. Beispielsweise für Schweizerdeutsch:\n\n1. Klicken Sie auf Start (oben links)\n2. Klicken Sie auf 00-About\n3. Klicken Sie auf Change-Keyboard-Layout-Permanently\n\nEin neues Fenster erscheint. Geben Sie dort "ch" ein und betätigen Sie die ENTER Taste. Nun ist ihre LiveCD auf Schweiz eingestellt, auch wenn Sie die LiveCD neu starten.\n\n![keyboard]-[4]-\n\n\n## Updates LiveCD\n\nBitte aktualisieren Sie Ihre LiveCD von Zeit zu Zeit. Hierfür gibt es einen Menü Eintrag, über welchen Sie die LiveCD aktualisieren können. Eine Internet Verbindung wird hierfür vorausgesetzt.\n\n1. Klicken Sie auf Start (oben links)\n2. Klicken Sie auf 00-About\n3. Update LiveCD\n\n![update]-[5]-\n\nDa Kali Linux viele Updates bringt, müssen Sie ca. 5-10 Minuten dafür einrechnen. Sie können aber auch während dem Update in Ihrer LiveCD weiter arbeiten.\n\n\n## Mauszeiger\nDrücken Sie die CTRL Taste auf der rechten Seite von Ihrer Tastatur, wenn Sie den Mauszeiger aus der VirtualBox befreien möchten.\n\n## Copy / Paste\nDie LiveCD sollte so eingestellt sein, dass Sie mit Copy und Paste arbeiten können. Falls Sie Probleme mit Copy und Paste haben, so kann man die Funktion in VirtualBox gesondert freischalten. Bitte prüfen Sie die Einstellungen unter VirtualBox unter "Gerät" (Gemeinsame Zwischenablage) und (Drag und Drop)\n\n## VPN\n\n![vpn]-[6]-\n\nUm auf die verwundbaren Systeme im Hacking-Lab zugreifen zu können, müssen Sie eine VPN Verbindung mit dem Hacking-Lab aufbauen. Sie sehen oben rechts im Menü ein VPN Icon. Bitte klicken Sie mit der rechten Maustaste auf das VPN Icon und wählen Sie "Connect Hacking-Lab". Sie müssen einen Benutzername und Passwort eingeben. Bitte registrieren Sie auf -[8]- einen Account und verwenden Sie für das VPN den genau gleichen Benutzernamen (e-mail) und Ihr gewähltes Passwort.-[1]-');
});

test('fillTranslatedText', () => {
    const input = '# Installation Hacking-Lab LiveCD\n' +
        '## Einleitung\n' +
        'Dieser Guide unterstützt Sie bei der Installation der Hacking-Lab LiveCD auf ihren Laptop oder Computer. Die LiveCD wird benötigt, um eine VPN Verbindung ins Hacking-Lab aufzubauen und basiert auf einem 64 Bit Kali Linux System. Darüber hinaus ist die LiveCD für diverse Übungen im Hacking-Lab optimiert und vorbereitet. Sie benötigen ein 64 Bit Laptop, damit die LiveCD korrekt funktioniert.\n' +
        '\n' +
        '\n' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```' +
        '## Pre-Requirement\n' +
        'Damit die LiveCD auf Ihrem Computer genutzt werden kann, muss die Hypervisor Funktion im BIOS von Ihrem PC aktiviert sein. Bitte stellen Sie dies zuerst sicher, ansonsten lässt sich die LiveCD nicht starten.\n' +
        '\n' +
        '![bios](bios.png)\n' +
        '\n' +
        '## Installation VirtualBox\n' +
        'Bitte laden Sie von http://www.virtualbox.org die neueste VirtualBox Software für Ihr Betriebssystem herunter. Folgen Sie der Installationsanleitung auf der Webseite. Für die Installation sind Administrator Rechte notwendig.\n' +
        '\n' +
        '## Download Hacking-Lab OVA\n' +
        'Bitte laden Sie von https://media.hacking-lab.com/installation/virtualbox/ die OVA Datei für Windows oder OSX herunter. Stellen Sie sicher, dass Sie die korrekte Version herunterladen. Prüfen Sie nach dem Download, dass die Datei die Dateiendung "ova" aufweist. Sollte sie "zip" sein, benennen Sie die Datei auf "ova" um.\n' +
        '\n' +
        '## Import LiveCD in VirtualBox\n' +
        'Bitte starten Sie VirtualBox und drücken Sie die Tasten "CTRL + I". Sie werden aufgefordert den Pfad anzugeben, wo sich die "ova" Datei befindet. Wählen Sie diese aus und bestätigen Sie die Eingabe. Sie können die Import Funktion auch via Menü über Datei -> Appliance Importieren starten. Nach ein paar Minuten "Import" steht Ihnen die LiveCD in VirtualBox zur Verfügung.\n' +
        '\n' +
        '## Starten LiveCD\n' +
        'Bitte starten Sie die LiveCD in VirtualBox. Es dauert ein paar Minuten und anschliessend werden Sie aufgefordert, einen Benutzername und Passwort einzugeben. Bitte verwenden Sie folgende Angaben:\n' +
        '\n' +
        'Benutzername = hacker\n' +
        'Passwort = compass\n' +
        '\n' +
        '![gui](gui.png)\n' +
        '\n' +
        'Nach dem ersten Login sehen Sie die graphische Oberfläche der Hacking-Lab LiveCD.\n' +
        '\n' +
        '\n' +
        '## Sprache der Tastatur\n' +
        'Die Tastatur in der LiveCD ist auf US English eingestellt. Sie können die Tastatur bequem über einen Menü Eintrag verändern. Beispielsweise für Schweizerdeutsch:\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Klicken Sie auf Change-Keyboard-Layout-Permanently\n' +
        '\n' +
        'Ein neues Fenster erscheint. Geben Sie dort "ch" ein und betätigen Sie die ENTER Taste. Nun ist ihre LiveCD auf Schweiz eingestellt, auch wenn Sie die LiveCD neu starten.\n' +
        '\n' +
        '![keyboard](keyboard.png)\n' +
        '\n' +
        '\n' +
        '## Updates LiveCD\n' +
        '\n' +
        'Bitte aktualisieren Sie Ihre LiveCD von Zeit zu Zeit. Hierfür gibt es einen Menü Eintrag, über welchen Sie die LiveCD aktualisieren können. Eine Internet Verbindung wird hierfür vorausgesetzt.\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Update LiveCD\n' +
        '\n' +
        '![update](update.png)\n' +
        '\n' +
        'Da Kali Linux viele Updates bringt, müssen Sie ca. 5-10 Minuten dafür einrechnen. Sie können aber auch während dem Update in Ihrer LiveCD weiter arbeiten.\n' +
        '\n' +
        '\n' +
        '## Mauszeiger\n' +
        'Drücken Sie die CTRL Taste auf der rechten Seite von Ihrer Tastatur, wenn Sie den Mauszeiger aus der VirtualBox befreien möchten.\n' +
        '\n' +
        '## Copy / Paste\n' +
        'Die LiveCD sollte so eingestellt sein, dass Sie mit Copy und Paste arbeiten können. Falls Sie Probleme mit Copy und Paste haben, so kann man die Funktion in VirtualBox gesondert freischalten. Bitte prüfen Sie die Einstellungen unter VirtualBox unter "Gerät" (Gemeinsame Zwischenablage) und (Drag und Drop)\n' +
        '\n' +
        '## VPN\n' +
        '\n' +
        '![vpn](vpn.png)\n' +
        '\n' +
        'Um auf die verwundbaren Systeme im Hacking-Lab zugreifen zu können, müssen Sie eine VPN Verbindung mit dem Hacking-Lab aufbauen. Sie sehen oben rechts im Menü ein VPN Icon. Bitte klicken Sie mit der rechten Maustaste auf das VPN Icon und wählen Sie "Connect Hacking-Lab". Sie müssen einen Benutzername und Passwort eingeben. Bitte registrieren Sie auf https://www.hacking-lab.com/ einen Account und verwenden Sie für das VPN den genau gleichen Benutzernamen (e-mail) und Ihr gewähltes Passwort.' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```';
    const object = extractNonTranslatableParts(input);
    const text = '# Installation Hacking-Lab LiveCD\n## Einleitung\nDieser Guide unterstützt Sie bei der Installation der Hacking-Lab LiveCD auf ihren Laptop oder Computer. Die LiveCD wird benötigt, um eine VPN Verbindung ins Hacking-Lab aufzubauen und basiert auf einem 64 Bit Kali Linux System. Darüber hinaus ist die LiveCD für diverse Übungen im Hacking-Lab optimiert und vorbereitet. Sie benötigen ein 64 Bit Laptop, damit die LiveCD korrekt funktioniert.\n\n\n-[0]-## Pre-Requirement\nDamit die LiveCD auf Ihrem Computer genutzt werden kann, muss die Hypervisor Funktion im BIOS von Ihrem PC aktiviert sein. Bitte stellen Sie dies zuerst sicher, ansonsten lässt sich die LiveCD nicht starten.\n\n![bios]-[2]-\n\n## Installation VirtualBox\nBitte laden Sie von -[9]- die neueste VirtualBox Software für Ihr Betriebssystem herunter. Folgen Sie der Installationsanleitung auf der Webseite. Für die Installation sind Administrator Rechte notwendig.\n\n## Download Hacking-Lab OVA\nBitte laden Sie von -[7]- die OVA Datei für Windows oder OSX herunter. Stellen Sie sicher, dass Sie die korrekte Version herunterladen. Prüfen Sie nach dem Download, dass die Datei die Dateiendung "ova" aufweist. Sollte sie "zip" sein, benennen Sie die Datei auf "ova" um.\n\n## Import LiveCD in VirtualBox\nBitte starten Sie VirtualBox und drücken Sie die Tasten "CTRL + I". Sie werden aufgefordert den Pfad anzugeben, wo sich die "ova" Datei befindet. Wählen Sie diese aus und bestätigen Sie die Eingabe. Sie können die Import Funktion auch via Menü über Datei -> Appliance Importieren starten. Nach ein paar Minuten "Import" steht Ihnen die LiveCD in VirtualBox zur Verfügung.\n\n## Starten LiveCD\nBitte starten Sie die LiveCD in VirtualBox. Es dauert ein paar Minuten und anschliessend werden Sie aufgefordert, einen Benutzername und Passwort einzugeben. Bitte verwenden Sie folgende Angaben:\n\nBenutzername = hacker\nPasswort = compass\n\n![gui]-[3]-\n\nNach dem ersten Login sehen Sie die graphische Oberfläche der Hacking-Lab LiveCD.\n\n\n## Sprache der Tastatur\nDie Tastatur in der LiveCD ist auf US English eingestellt. Sie können die Tastatur bequem über einen Menü Eintrag verändern. Beispielsweise für Schweizerdeutsch:\n\n1. Klicken Sie auf Start (oben links)\n2. Klicken Sie auf 00-About\n3. Klicken Sie auf Change-Keyboard-Layout-Permanently\n\nEin neues Fenster erscheint. Geben Sie dort "ch" ein und betätigen Sie die ENTER Taste. Nun ist ihre LiveCD auf Schweiz eingestellt, auch wenn Sie die LiveCD neu starten.\n\n![keyboard]-[4]-\n\n\n## Updates LiveCD\n\nBitte aktualisieren Sie Ihre LiveCD von Zeit zu Zeit. Hierfür gibt es einen Menü Eintrag, über welchen Sie die LiveCD aktualisieren können. Eine Internet Verbindung wird hierfür vorausgesetzt.\n\n1. Klicken Sie auf Start (oben links)\n2. Klicken Sie auf 00-About\n3. Update LiveCD\n\n![update]-[5]-\n\nDa Kali Linux viele Updates bringt, müssen Sie ca. 5-10 Minuten dafür einrechnen. Sie können aber auch während dem Update in Ihrer LiveCD weiter arbeiten.\n\n\n## Mauszeiger\nDrücken Sie die CTRL Taste auf der rechten Seite von Ihrer Tastatur, wenn Sie den Mauszeiger aus der VirtualBox befreien möchten.\n\n## Copy / Paste\nDie LiveCD sollte so eingestellt sein, dass Sie mit Copy und Paste arbeiten können. Falls Sie Probleme mit Copy und Paste haben, so kann man die Funktion in VirtualBox gesondert freischalten. Bitte prüfen Sie die Einstellungen unter VirtualBox unter "Gerät" (Gemeinsame Zwischenablage) und (Drag und Drop)\n\n## VPN\n\n![vpn]-[6]-\n\nUm auf die verwundbaren Systeme im Hacking-Lab zugreifen zu können, müssen Sie eine VPN Verbindung mit dem Hacking-Lab aufbauen. Sie sehen oben rechts im Menü ein VPN Icon. Bitte klicken Sie mit der rechten Maustaste auf das VPN Icon und wählen Sie "Connect Hacking-Lab". Sie müssen einen Benutzername und Passwort eingeben. Bitte registrieren Sie auf -[8]- einen Account und verwenden Sie für das VPN den genau gleichen Benutzernamen (e-mail) und Ihr gewähltes Passwort.-[1]-';
    const output = fillTranslatedText(text, object.array);
    expect(output).toEqual('# Installation Hacking-Lab LiveCD\n' +
        '## Einleitung\n' +
        'Dieser Guide unterstützt Sie bei der Installation der Hacking-Lab LiveCD auf ihren Laptop oder Computer. Die LiveCD wird benötigt, um eine VPN Verbindung ins Hacking-Lab aufzubauen und basiert auf einem 64 Bit Kali Linux System. Darüber hinaus ist die LiveCD für diverse Übungen im Hacking-Lab optimiert und vorbereitet. Sie benötigen ein 64 Bit Laptop, damit die LiveCD korrekt funktioniert.\n' +
        '\n' +
        '\n' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```' +
        '## Pre-Requirement\n' +
        'Damit die LiveCD auf Ihrem Computer genutzt werden kann, muss die Hypervisor Funktion im BIOS von Ihrem PC aktiviert sein. Bitte stellen Sie dies zuerst sicher, ansonsten lässt sich die LiveCD nicht starten.\n' +
        '\n' +
        '![bios](bios.png)\n' +
        '\n' +
        '## Installation VirtualBox\n' +
        'Bitte laden Sie von http://www.virtualbox.org die neueste VirtualBox Software für Ihr Betriebssystem herunter. Folgen Sie der Installationsanleitung auf der Webseite. Für die Installation sind Administrator Rechte notwendig.\n' +
        '\n' +
        '## Download Hacking-Lab OVA\n' +
        'Bitte laden Sie von https://media.hacking-lab.com/installation/virtualbox/ die OVA Datei für Windows oder OSX herunter. Stellen Sie sicher, dass Sie die korrekte Version herunterladen. Prüfen Sie nach dem Download, dass die Datei die Dateiendung "ova" aufweist. Sollte sie "zip" sein, benennen Sie die Datei auf "ova" um.\n' +
        '\n' +
        '## Import LiveCD in VirtualBox\n' +
        'Bitte starten Sie VirtualBox und drücken Sie die Tasten "CTRL + I". Sie werden aufgefordert den Pfad anzugeben, wo sich die "ova" Datei befindet. Wählen Sie diese aus und bestätigen Sie die Eingabe. Sie können die Import Funktion auch via Menü über Datei -> Appliance Importieren starten. Nach ein paar Minuten "Import" steht Ihnen die LiveCD in VirtualBox zur Verfügung.\n' +
        '\n' +
        '## Starten LiveCD\n' +
        'Bitte starten Sie die LiveCD in VirtualBox. Es dauert ein paar Minuten und anschliessend werden Sie aufgefordert, einen Benutzername und Passwort einzugeben. Bitte verwenden Sie folgende Angaben:\n' +
        '\n' +
        'Benutzername = hacker\n' +
        'Passwort = compass\n' +
        '\n' +
        '![gui](gui.png)\n' +
        '\n' +
        'Nach dem ersten Login sehen Sie die graphische Oberfläche der Hacking-Lab LiveCD.\n' +
        '\n' +
        '\n' +
        '## Sprache der Tastatur\n' +
        'Die Tastatur in der LiveCD ist auf US English eingestellt. Sie können die Tastatur bequem über einen Menü Eintrag verändern. Beispielsweise für Schweizerdeutsch:\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Klicken Sie auf Change-Keyboard-Layout-Permanently\n' +
        '\n' +
        'Ein neues Fenster erscheint. Geben Sie dort "ch" ein und betätigen Sie die ENTER Taste. Nun ist ihre LiveCD auf Schweiz eingestellt, auch wenn Sie die LiveCD neu starten.\n' +
        '\n' +
        '![keyboard](keyboard.png)\n' +
        '\n' +
        '\n' +
        '## Updates LiveCD\n' +
        '\n' +
        'Bitte aktualisieren Sie Ihre LiveCD von Zeit zu Zeit. Hierfür gibt es einen Menü Eintrag, über welchen Sie die LiveCD aktualisieren können. Eine Internet Verbindung wird hierfür vorausgesetzt.\n' +
        '\n' +
        '1. Klicken Sie auf Start (oben links)\n' +
        '2. Klicken Sie auf 00-About\n' +
        '3. Update LiveCD\n' +
        '\n' +
        '![update](update.png)\n' +
        '\n' +
        'Da Kali Linux viele Updates bringt, müssen Sie ca. 5-10 Minuten dafür einrechnen. Sie können aber auch während dem Update in Ihrer LiveCD weiter arbeiten.\n' +
        '\n' +
        '\n' +
        '## Mauszeiger\n' +
        'Drücken Sie die CTRL Taste auf der rechten Seite von Ihrer Tastatur, wenn Sie den Mauszeiger aus der VirtualBox befreien möchten.\n' +
        '\n' +
        '## Copy / Paste\n' +
        'Die LiveCD sollte so eingestellt sein, dass Sie mit Copy und Paste arbeiten können. Falls Sie Probleme mit Copy und Paste haben, so kann man die Funktion in VirtualBox gesondert freischalten. Bitte prüfen Sie die Einstellungen unter VirtualBox unter "Gerät" (Gemeinsame Zwischenablage) und (Drag und Drop)\n' +
        '\n' +
        '## VPN\n' +
        '\n' +
        '![vpn](vpn.png)\n' +
        '\n' +
        'Um auf die verwundbaren Systeme im Hacking-Lab zugreifen zu können, müssen Sie eine VPN Verbindung mit dem Hacking-Lab aufbauen. Sie sehen oben rechts im Menü ein VPN Icon. Bitte klicken Sie mit der rechten Maustaste auf das VPN Icon und wählen Sie "Connect Hacking-Lab". Sie müssen einen Benutzername und Passwort eingeben. Bitte registrieren Sie auf https://www.hacking-lab.com/ einen Account und verwenden Sie für das VPN den genau gleichen Benutzernamen (e-mail) und Ihr gewähltes Passwort.' +
        '```' +
        'function () {' +
        'sadfasdf' +
        'asjdlkfjöldsf' +
        'zdsaiufhdsakj' +
        'asdfui78789' +
        '}' +
        '```');
});

test('fillSectionTranslation', () => {
    const sectionTranslationArray = fillSectionTranslation([], {sectionId: '09lk3214-2134324jfksdf-adsf892-2ds34ssdf', translation: 'Test translation'});
    expect(sectionTranslationArray[0].translation).toEqual('Test translation');
});

test('fillSectionTranslation already filled', () => {
    let sectionTranslationArray = fillSectionTranslation([], {sectionId: '09lk3214-2134324jfksdf-adsf892-2ds34ssdf', translation: 'Test translation'});
    sectionTranslationArray = fillSectionTranslation(sectionTranslationArray, {sectionId: '089dsfjk14-21poq6nj87ksdf-a0891292-2ds34ssdf', translation: 'Test translation section 2'});
    expect(sectionTranslationArray[1].translation).toEqual('Test translation section 2');
});


test('fillStepTranslation', () => {
    const stepTranslationArray = fillStepTranslation([], {sectionId: '09lk3214-2134324jfksdf-adsf892-2ds34ssdf', translation: 'Test translation'});
    expect(stepTranslationArray[0].translation).toEqual('Test translation');
});

test('fillStepTranslation already filled', () => {
    let stepTranslationArray = fillStepTranslation([], {stepId: '09lk3214-2134324jfksdf-adsf892-2ds34ssdf', translation: 'Test translation', type: 'hint'});
    stepTranslationArray = fillStepTranslation(stepTranslationArray, {stepId: '089dsfjk14-21poq6nj87ksdf-a0891292-2ds34ssdf', translation: 'Test translation step 2', type: 'instruction'});
    expect(stepTranslationArray[1].translation).toEqual('Test translation step 2');
});

test('parseJsonData', () => {
    const output = parseJsonData('test');
    expect(output).toEqual('');
});
