{ sources ? import ./nix/sources.nix }:
let
  pkgs =
    import sources.nixpkgs {
      overlays = [
      ];
    };

in
pkgs.mkShell {
  buildInputs = [
    # JavaScript
    pkgs.nodejs-16_x
    pkgs.yarn

    # Python
  ];

  shellHook = ''
  '';
}
